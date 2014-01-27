package net.mortalsilence.indiepim.server.command.handler;

import com.sun.mail.imap.AppendUID;
import com.sun.mail.imap.IMAPFolder;
import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.DeleteMessages;
import net.mortalsilence.indiepim.server.command.results.DeleteMessagesResult;
import net.mortalsilence.indiepim.server.command.results.DeleteResultInfo;
import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.dao.UserDAO;
import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import net.mortalsilence.indiepim.server.domain.MessagePO;
import net.mortalsilence.indiepim.server.domain.MessageTagLineageMappingPO;
import net.mortalsilence.indiepim.server.domain.TagLineagePO;
import net.mortalsilence.indiepim.server.message.ConnectionUtils;
import net.mortalsilence.indiepim.server.message.ImapMsgOperationCallback;
import net.mortalsilence.indiepim.server.message.MessageConstants;
import net.mortalsilence.indiepim.server.message.MessageUpdateService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.mail.*;
import java.util.*;

@Service
public class DeleteMessagesHandler implements Command<DeleteMessages, DeleteMessagesResult> {

    @Inject private MessageUpdateService messageUpdateService;
    @Inject private MessageDAO messageDAO;
    @Inject private UserDAO userDAO;
    @Inject private GenericDAO genericDAO;
    @Inject private ConnectionUtils connectionUtils;

    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");

	@Transactional
    @Override
    public DeleteMessagesResult execute(DeleteMessages action) {
		final List<Long> messageIds = action.getMessageIds();
        final Long userId = ActionUtils.getUserId();
        final List<MessagePO> messages = messageDAO.getMessagesByIdAndUser(messageIds, userId, true);
		// TODO refactor result to handle multiple tag lineages per message
        final Map<Long, DeleteResultInfo> result = new HashMap<Long, DeleteResultInfo>();
        List<MessagePO> accountMessages = new LinkedList<MessagePO>();

		if(messages.isEmpty())
			return new DeleteMessagesResult();
		Long accountId = messages.get(0).getMessageAccount().getId();
		int i=0;

        for(final MessagePO curMsg : messages){
			/* collect messages per account (performance) if there are some left */
            if(accountId.equals(curMsg.getMessageAccount().getId()) && i < messages.size() -1) {
				accountMessages.add(curMsg);
			} else {
				// TODO give accountMessages and not messages as parameter 
                final MessageAccountPO account = messageDAO.getMessageAccount(userId, accountId);
                final boolean expunge = account.getDeleteMode().equals(MessageConstants.MESSAGE_DELETE_MODE.MOVE_2_TRASH) ||
                        account.getDeleteMode().equals(MessageConstants.MESSAGE_DELETE_MODE.EXPUNGE);

                final long start = System.currentTimeMillis();
                final Set<MessageTagLineageMappingPO> tagLineageMappingsToDelete = new LinkedHashSet<MessageTagLineageMappingPO>();
                messageUpdateService.updateImapMessages(userId, messages, accountId, expunge, new ImapMsgOperationCallback() {
                    // TODO performance tuning
                    @Override
                    public void processMessage(IMAPFolder folder, Message imapMessage, Long messageUID, MessagePO indieMessage, MessageTagLineageMappingPO tagLineageMapping) throws MessagingException {
                        try {
                            if (account.getDeleteMode().equals(MessageConstants.MESSAGE_DELETE_MODE.MOVE_2_TRASH)) {
                                // MOVE to trash folder
                                final Store store = folder.getStore();
                                final Folder trashFolder = store.getFolder(connectionUtils.getTrashFolderPath(account, store));
                                if(trashFolder.getFullName().equals(folder.getFullName())) {
                                    // message already in trash, ignore
                                    if(logger.isDebugEnabled()) {
                                        logger.debug("Message with UID " + messageUID + " already in trash. Ignoring.");
                                    }
                                    result.put(indieMessage.getId(), new DeleteResultInfo(false, "Message already in trash. Ignored."));
                                    return;
                                }
                                if (logger.isDebugEnabled())
                                    logger.debug("copying IMAP message whith UID " + messageUID + " from folder '" + folder.getName() + "' to trash folder '" + trashFolder + "'.");
                                if (!trashFolder.exists()) {
                                    if (logger.isDebugEnabled())
                                        logger.debug("Configured trashfolder '" + trashFolder.getFullName() + "' does not exists. Creating...");
                                    boolean creationResult = trashFolder.create(Folder.HOLDS_FOLDERS | Folder.HOLDS_MESSAGES);
                                    if (!creationResult)
                                        throw new RuntimeException("Error creating trash folder '" + trashFolder.getFullName() + "'");
                                }
                                // TODO fallback when this is not supported by the server!
                                final AppendUID[] copyResult = folder.copyUIDMessages(new Message[]{imapMessage}, trashFolder);

                                TagLineagePO trashFolderTagLineage;
                                if ((trashFolderTagLineage = account.getTrashFolder()) == null)
                                    trashFolderTagLineage = connectionUtils.getOrCreateTagLineage(userDAO.getUser(userId), account, trashFolder);

                                // defer the deletion of the tag lineage mappings, since the caller of the callback keeps them in an iterator
                                tagLineageMappingsToDelete.add(tagLineageMapping);
                                messageDAO.addTagLineage(indieMessage, trashFolderTagLineage, copyResult[0].uid);
                            }

                            if (logger.isDebugEnabled())
                                logger.debug("Marking IMAP message with UID " + messageUID + " as deleted.");
                            imapMessage.setFlag(Flags.Flag.DELETED, true); // always mark the original message as deleted

                            if (account.getDeleteMode().equals(MessageConstants.MESSAGE_DELETE_MODE.EXPUNGE)) {
                                final List<Long> msgIds = new LinkedList<Long>();
                                msgIds.add(indieMessage.getId());
                                if (logger.isDebugEnabled())
                                    logger.debug("Deleting MessagePO with id " + indieMessage.getId());
                                messageDAO.deleteMessages(userId, msgIds);
                            } else if (account.getDeleteMode().equals(MessageConstants.MESSAGE_DELETE_MODE.MARK_DELETED)) {
                                indieMessage.setDeleted(true);
                            }
                            result.put(indieMessage.getId(), new DeleteResultInfo(true));
                        } catch(Exception e) {
                            logger.error("Deletion of message with UID " + messageUID + " from folder " + folder.getFullName() + " failed.", e);
                            result.put(indieMessage.getId(), new DeleteResultInfo(false, e.getMessage()));
                        }
                    }
                });

                final Iterator<MessageTagLineageMappingPO> it = tagLineageMappingsToDelete.iterator();
                while(it.hasNext()) {
                    final MessageTagLineageMappingPO curTagLineageMapping = it.next();
                    final Long msgUid = curTagLineageMapping.getMsgUid();
                    if(logger.isDebugEnabled())
                        logger.debug("Removing tag lineage " + curTagLineageMapping.getTagLineage().getLineage() + " from message with UID " + curTagLineageMapping.getMsgUid());
                    it.remove();
                    final MessagePO indieMessage = curTagLineageMapping.getMessage();
                    final Long msgId = indieMessage.getId();
                    indieMessage.getMsgTagLineageMappings().remove(curTagLineageMapping);
                    // TODO not working
                    genericDAO.remove(curTagLineageMapping);
                    if(!result.containsKey(msgId)) {
                        // return true to get the message deleted from the clients views
                        result.put(msgId, new DeleteResultInfo(true, "Message not found on server. Deleted locally."));
                    }
                }

                if(logger.isDebugEnabled())
                    logger.debug("messageUpdateService.updateImapMessages (delete) took " + (System.currentTimeMillis() - start) + " ms.");

                accountId = curMsg.getMessageAccount().getId();
                accountMessages = new LinkedList<MessagePO>();
			}
			i++;
		}
		return new DeleteMessagesResult(result);
	}


	@Override
	public void rollback(DeleteMessages arg0, DeleteMessagesResult arg1) {
		// TODO how to roll back?
	}

}
