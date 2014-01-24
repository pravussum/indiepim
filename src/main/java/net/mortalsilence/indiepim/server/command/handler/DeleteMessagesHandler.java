package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.DeleteMessages;
import net.mortalsilence.indiepim.server.command.results.BooleanResult;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.dao.UserDAO;
import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import net.mortalsilence.indiepim.server.domain.MessagePO;
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
import java.util.LinkedList;
import java.util.List;

@Service
public class DeleteMessagesHandler implements Command<DeleteMessages, BooleanResult> {

    @Inject private MessageUpdateService messageUpdateService;
    @Inject private MessageDAO messageDAO;
    @Inject private UserDAO userDAO;
    @Inject private ConnectionUtils connectionUtils;

    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");

	@Transactional
    @Override
    public BooleanResult execute(DeleteMessages action) {
		final List<Long> messageIds = action.getMessageIds();
        final Long userId = ActionUtils.getUserId();
        final List<MessagePO> messages = messageDAO.getMessagesByIdAndUser(messageIds, userId, true);
		List<MessagePO> accountMessages = new LinkedList<MessagePO>();

		if(messages.isEmpty())
			return new BooleanResult(false);
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
                messageUpdateService.updateImapMessages(userId, messages, accountId, expunge, new ImapMsgOperationCallback() {
                    // TODO performance tuning
                    @Override
                    public void processMessage(Folder folder, Message imapMessage, Long messageUID, MessagePO indieMessage) throws MessagingException {
                        if (account.getDeleteMode().equals(MessageConstants.MESSAGE_DELETE_MODE.MOVE_2_TRASH)) {
                            // MOVE to trash folder
                            final Store store = folder.getStore();
                            final Folder trashFolder = store.getFolder(connectionUtils.getTrashFolderPath(account, store));
                            if(logger.isDebugEnabled())
                                logger.debug("copying IMAP message whith UID " + messageUID + " from folder '" + folder.getName() + "' to trash folder '" + trashFolder + "'.");
                            folder.copyMessages(new Message[]{imapMessage}, trashFolder);

                            TagLineagePO trashFolderTagLineage;
                            if((trashFolderTagLineage = account.getTrashFolder()) == null)
                                trashFolderTagLineage = connectionUtils.getOrCreateTagLineage(userDAO.getUser(userId), account, trashFolder);

                            // TODO update tag lineage of indieMessage immediately - beware, the target tag lineage it might not yet be created
                            //  indieMessage.addTagLineage(trashFolderTagLineage);
                        }

                        if(logger.isDebugEnabled())
                            logger.debug("Marking IMAP message with UID " + messageUID + " as deleted.");
                        imapMessage.setFlag(Flags.Flag.DELETED, true); // always mark the original message as deleted

                        if(account.getDeleteMode().equals(MessageConstants.MESSAGE_DELETE_MODE.EXPUNGE)) {
                            final List<Long> msgIds = new LinkedList<Long>();
                            msgIds.add(indieMessage.getId());
                            if(logger.isDebugEnabled())
                                logger.debug("Deleting MessagePO with id " + indieMessage.getId());
                            messageDAO.deleteMessages(userId, msgIds);
                        } else if(account.getDeleteMode().equals(MessageConstants.MESSAGE_DELETE_MODE.MARK_DELETED)){
                            indieMessage.setDeleted(true);
                        }
                    }
                });
                if(logger.isDebugEnabled())
                    logger.debug("messageUpdateService.updateImapMessages (delete) took " + (System.currentTimeMillis() - start) + " ms.");

                accountId = curMsg.getMessageAccount().getId();
                accountMessages = new LinkedList<MessagePO>();
			}
			i++;
		}
		return new BooleanResult(true);
	}


	@Override
	public void rollback(DeleteMessages arg0, BooleanResult arg1) {
		// TODO how to roll back?
	}

}
