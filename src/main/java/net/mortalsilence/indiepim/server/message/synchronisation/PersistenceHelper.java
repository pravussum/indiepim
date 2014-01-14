package net.mortalsilence.indiepim.server.message.synchronisation;

import net.mortalsilence.indiepim.server.SharedConstants;
import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.dao.TagDAO;
import net.mortalsilence.indiepim.server.domain.*;
import net.mortalsilence.indiepim.server.utils.MessageUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import java.util.*;

@Service
/**
 * Helper class to fulfill certain task in their own new transaction and comit it immediately
 * without interferring with the surrounding transaction
 * This is primarily useful for persisting single messages in a long running account sync transaction.
 * A rollback for a single message transaction shouldn't affect the whole sync process.
 * The reason we didn't use nested transactions here is that a persisted message should immediately be
 * visible in the message list, since a account sync can take very long. Furthermore are nested transactions
 * only commited at the very end of the surrounding transaction an thus lost, if the sync process is canceled or rolled
 * back.
 */
public class PersistenceHelper {

    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");
    @Inject private MessageUtils messageUtils;
    @Inject private MessageDAO messageDAO;
    @Inject private GenericDAO genericDAO;
    @Inject private TagDAO tagDAO;

    @Transactional(propagation = Propagation.REQUIRED)
    /* made public for transactional annotation to work */
    public boolean persistMessage(final MessageAccountPO account,
                                  final Folder folder,
                                  final TagLineagePO tagLineage,
                                  final IncomingMessageHandler updateHandler,
                                  final IncomingMessageHandler persistHandler,
                                  final IncomingMessageHandler addressHandler,
                                  final Session session,
                                  final UserPO user,
                                  final Long msgUid,
                                  final Message message,
                                  final Set<String> hashCache) {
        boolean isNew = false;
        try {
            if(logger.isInfoEnabled())
                logger.info("MSG_UID: " + msgUid);
            final String hash = messageUtils.getHash(messageUtils.getSenderStr(message),
                    messageUtils.getReceiverStr(message),
                    message.getSubject(),
                    message.getReceivedDate());
            if(hashCache.contains(hash) || hasDuplicate(account, hash)) {
                /* Update existing message in different folder */
                final MessagePO duplicate = getDuplicate(account, hash);
                if(!duplicate.hasTagLineage(tagLineage, msgUid)){
                    duplicate.addTagLineage(tagLineage, msgUid);
                }
                // TODO: replace by something cool
                if(updateHandler != null)
                    updateHandler.handleMessage(message, duplicate, msgUid, account, folder, tagLineage, session, user);
            } else {
                /* handle new messages */
                MessagePO newMessage = new MessagePO();
                newMessage.setHash(hash);
                persistHandler.handleMessage(message, newMessage, msgUid, account, folder, tagLineage, session, user);
                addressHandler.handleMessage(message, newMessage, msgUid, account, folder, tagLineage, session, user);
                isNew = true;
            }
            hashCache.add(hash);
        } catch (Exception e) {
            String folderName;
            try {
                folderName = folder.getFullName();
            } catch(Exception e2){
                folderName = "<Could not get message folder name>";
            }
            logger.error("Error synchronizing message " + msgUid + " in folder " + folderName, e);
        }
        return isNew;
    }

    /**
     * This helper method is to allow tag lineage creation to happen in an own immediately commited transaction
     * Otherwise, DB locks would be held for the newly created rows which would cause a deadlock during message
     * persistence, since these transactions are completely independent and the outer transaction (holding the lock)
     * is suspended in the meanwhile.
     * @param user
     * @param account
     * @param folder
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public TagLineagePO getOrCreateTagLineage(final UserPO user, final MessageAccountPO account, final Folder folder) {
   		final List<String> tags = new LinkedList<String>();
   		try {
   			StringTokenizer st = new StringTokenizer(folder.getFullName(), new Character(folder.getSeparator()).toString());
   			while(st.hasMoreElements()) {
   				tags.add(st.nextToken());
   			}
   		} catch (MessagingException e) {
   			e.printStackTrace();
   			throw new RuntimeException("Error parsing folder name " + folder.getFullName(), e);
   		}

   		if(account.getTagHierarchy() == null) {
   			createTagHierarchy4Account(user, account);
   		}

   		final List<String> partialTagLineageTags = new LinkedList<String>();
   		final Iterator<String> tagIt = tags.iterator();
   		TagLineagePO tagLineage = null;
   		while(tagIt.hasNext()) {
   			partialTagLineageTags.add(tagIt.next());
   			final String tagLineageStr = StringUtils.join(partialTagLineageTags, SharedConstants.TAG_LINEAGE_SEPARATOR);
   			tagLineage = tagDAO.getOrCreateTagLineage(user, tagLineageStr, account.getTagHierarchy());
   		}

   		return tagLineage;
   	}

    @Transactional(propagation = Propagation.REQUIRED)
    public Date updateLastSyncRun(final UserPO user, final long accountId) {
        final MessageAccountPO account = messageDAO.getMessageAccount(user.getId(), accountId);
        final Date lastSyncRun = Calendar.getInstance().getTime();
        account.getMessageAccountStats().setLastSyncRun(lastSyncRun);
        return lastSyncRun;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void markMessageAsRead(final Long userId, final Long msgId, final Long tagLineageId, final Boolean readFlag) {
        messageDAO.getMessageByUIDAndTagLineage(userId, msgId, tagLineageId).setRead(readFlag);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteMessagesForTagLineage(final Long userId, final Collection<Long> msgIds, Long tagLineageId) {
        return messageDAO.deleteMessagesForTagLineage(userId, msgIds, tagLineageId);
    }

   	private void createTagHierarchy4Account(final UserPO user, final MessageAccountPO account) {
   		TagHierarchyPO tagHierarchy = new TagHierarchyPO();
   		tagHierarchy.setUser(user);
   		tagHierarchy.setName(account.getTag().getTag());
   		genericDAO.persist(tagHierarchy);
   		account.setTagHierarchy(tagHierarchy);
   	}

    private MessagePO getDuplicate(final MessageAccountPO account, final String hash) throws MessagingException {
   		return messageDAO.getMessageFromHash(account.getUser().getId(), account.getId(), hash);
   	}

   	private Boolean hasDuplicate(final MessageAccountPO account, final String hash) throws MessagingException {
   		return messageDAO.existsMessageWithHash(account.getUser().getId(), account.getId(), hash);
   	}

}
