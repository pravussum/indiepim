package net.mortalsilence.indiepim.server.message.synchronisation;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPFolder.FetchProfileItem;
import net.mortalsilence.indiepim.server.comet.AccountSyncProgressMessage;
import net.mortalsilence.indiepim.server.comet.AccountSyncedMessage;
import net.mortalsilence.indiepim.server.comet.CometService;
import net.mortalsilence.indiepim.server.comet.NewMsgMessage;
import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.dao.TagDAO;
import net.mortalsilence.indiepim.server.domain.*;
import net.mortalsilence.indiepim.server.message.ConnectionUtils;
import net.mortalsilence.indiepim.server.message.MessageConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SynchroService implements MessageConstants {

	final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");
	private static final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<String, ReentrantLock>(); 
	
	private static final Set<String> hashCache = new HashSet<String>();
	private static long cometEventTime;
    @Inject private MessageDAO messageDAO;
    @Inject private TagDAO tagDAO;
    @Inject private ConnectionUtils connectionUtils;
    @Inject private PersistMessageHandler persistMessageHandler;
    @Inject private UpdateFlagsAndFoldersHandler updateFlagsAndFoldersHandler;
    @Inject private MailAddressHandler mailAddressHandler;
    @Inject private CometService cometService;
    @Inject private PersistenceHelper persistenceHelper;
    @Inject private GenericDAO genericDAO;

	public boolean synchronize(	final UserPO user,
								final MessageAccountPO account,
								final SyncUpdateMethod updateMode) {

		if(logger.isDebugEnabled())
			logger.debug("Starting synchronisation for account " + account.getName());
		long overallTime = System.currentTimeMillis();
        final Long accountId = account.getId();
        cometEventTime = overallTime;

        if(user == null || account == null) {
            throw new IllegalArgumentException();
        }
        String lockKey = user.getId().toString() + "_" + accountId.toString();
		ReentrantLock newLock = new ReentrantLock();
		ReentrantLock lock = lockMap.putIfAbsent(lockKey, newLock);
		if(lock == null) {
			lock = newLock; 
		}
		logger.info("Acquiring lock with key " + lockKey);
		lock.lock();
		logger.info("Lock " + lockKey + " acquired.");
		try {
			boolean newMessages = false;
			hashCache.clear();
			
			final Session session = connectionUtils.getSession(account, true);
			final Store store = connectionUtils.connectToStore(account, session);
			/* Handlers */
			final IncomingMessageHandler updateHandler;
            if(updateMode == SyncUpdateMethod.FLAGS)
                updateHandler = updateFlagsAndFoldersHandler;
            else if (updateMode == SyncUpdateMethod.FULL)
                updateHandler = persistMessageHandler;
            else if(updateMode == SyncUpdateMethod.NONE)
                updateHandler = null;
            else throw new IllegalArgumentException();

			Folder folder = null;
			try {
				Folder[] folders = store.getDefaultFolder().list("*");
				for (int j=0; j<folders.length; j++) {
					folder = folders[j];
					if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
						if(logger.isInfoEnabled())
							logger.info("Synchronizing folder: ##### " + folder.getFullName() + " #####");						
						folder.open(Folder.READ_ONLY);
						long time = System.currentTimeMillis();
						/* Tag lineage */
						final TagLineagePO tagLineage = persistenceHelper.getOrCreateTagLineage(user, account, folder);
						
						final Map<Long, Message> uidMsgMap = getMessageUids((IMAPFolder)folder, updateMode);

						final Set<Long> remoteUids = uidMsgMap.keySet();
						final List<Long> knownUids = tagDAO.getAllMsgUidsForTagLineage(user.getId(), tagLineage.getId());
						
						
						/* Handle new Messages */
						@SuppressWarnings("unchecked")
						final Collection<Long> newUids = ListUtils.removeAll(remoteUids, knownUids);
						cometService.sendCometMessages(account.getUser().getId(), new AccountSyncProgressMessage(account.getUser().getId(), accountId, folder.getFullName(), newUids.size(), 0));
						if(newUids.size() > 0) {
                            // ignore failed messages
                            newMessages |= handleNewMessages(uidMsgMap, newUids, account, folder, tagLineage, updateHandler, persistMessageHandler, mailAddressHandler, session, user);
						}
						
						/* Handle message updates if applicable */
						@SuppressWarnings("unchecked")
						final Collection<Long> updatedUids = CollectionUtils.intersection(remoteUids, knownUids);
						
						//handleMessageUpdates(uidMsgMap, updatedUids, account, folder, tagLineage, updateHandler, addressHandler, updateMode, session);
						final Map<Long, Boolean> dbReadFlagMap = isUpdateFlags(updateMode) ? messageDAO.getMsgUidFlagMapForTagLineage(user.getId(), tagLineage.getId()) : null;
						final Iterator<Long> it = updatedUids.iterator();
						long msgUpdateTime = 0;
						if(logger.isDebugEnabled())
							msgUpdateTime = System.currentTimeMillis();
						while(it.hasNext()) {
							final Long curMsgId = it.next();
							if(isUpdateFlags(updateMode)) {
								final boolean imapReadFlag = uidMsgMap.get(curMsgId).isSet(Flags.Flag.SEEN);
								// only update db message when read flags differ 
								if(imapReadFlag != dbReadFlagMap.get(curMsgId)) {
									if(logger.isDebugEnabled())
										logger.debug("Updating READ-Flag of existing message " + curMsgId);
                                    // TODO performance issue: this is VERY slow when updating lots of messages
                                    persistenceHelper.markMessageAsRead(user.getId(), curMsgId, tagLineage.getId(), imapReadFlag);
								}
							}
						}
						if(logger.isDebugEnabled())
							logger.debug("Message Update took: " + (System.currentTimeMillis() - msgUpdateTime) + "ms.");				
						
						/* Handle deleted messages (remove tag lineage from message) */
						@SuppressWarnings("unchecked")
						final Collection<Long> deleteUids = ListUtils.removeAll(knownUids, remoteUids); /* CollectionUtils.removeAll seems to be buggy (calls wrong ListUtils method) */
						if(!deleteUids.isEmpty())
                            handleDeletedMessages(deleteUids, user, tagLineage);

						// TODO recognize messages moved to another folder by another client (new message UID and tag lineage). Use unique business key (from/to/receivedDate)? Performance?
	
						if(logger.isInfoEnabled())
							logger.info("Folder synchronisation took " + (System.currentTimeMillis() - time) + "ms.");
						folder.close(false);
								
					}
				}

				final Date lastSyncRun = persistenceHelper.updateLastSyncRun(user, account.getId());

				/* Send Comet messages to clients */
				if(newMessages) {
					cometService.sendCometMessages(user.getId(), new NewMsgMessage/*LOL*/(user.getId(), accountId));
				}
				cometService.sendCometMessages(user.getId(), new AccountSyncedMessage(user.getId(), accountId, lastSyncRun));

				if(logger.isDebugEnabled())
					logger.debug("Overall sync duration: " + (System.currentTimeMillis() - overallTime) + "ms.");				
				return newMessages;
				
			} catch (AuthenticationFailedException e) {
				e.printStackTrace();
				throw new RuntimeException("Account synchronisation failed (Authentication failure): Username or Password wrong?");
			} catch (MessagingException e) {
				e.printStackTrace();
				throw new RuntimeException("Account synchronisation failed: " + e.getMessage());		
			} finally {
				if(folder != null && folder.isOpen()) {
					try {
						folder.close(false);
					} catch (MessagingException e) {
						e.printStackTrace();
						// Ignore
					}
				}
				if(store != null) {
					try {
						store.close();
					} catch (MessagingException e) {
						e.printStackTrace();
						// Ignore
					}
				}
			}
		} finally {
			logger.info("Releasing lock with key " + lockKey);
			lock.unlock();			
		}
	}

	private boolean isUpdateFlags(SyncUpdateMethod updateMode) {
		return SyncUpdateMethod.FLAGS.equals(updateMode) || SyncUpdateMethod.FULL.equals(updateMode);
	}

	private boolean handleNewMessages(final Map<Long, Message> uidMsgMap,
                                      final Collection<Long> uids,
                                      MessageAccountPO account,
                                      final Folder folder,
                                      TagLineagePO tagLineage,
                                      IncomingMessageHandler updateHandler,
                                      IncomingMessageHandler persistHandler,
                                      IncomingMessageHandler addressHandler,
                                      Session session,
                                      UserPO user) {

		boolean newMessages = false;

		final Iterator<Long> it = uids.iterator();
		int i = 0;
		while(it.hasNext()) {
			/* Update clients every second */
			i++;
            if(System.currentTimeMillis() - cometEventTime > 1000) {
				cometService.sendCometMessages(account.getUser().getId(), new AccountSyncProgressMessage(account.getUser().getId(), account.getId(), folder.getFullName(), uids.size(), i));
				cometEventTime = System.currentTimeMillis();
			}
			Long msgUid = it.next();
			final Message message = uidMsgMap.get(msgUid);

            try {
                newMessages |= persistenceHelper.persistMessage(account,
                        folder,
                        tagLineage,
                        updateHandler,
                        persistHandler,
                        addressHandler,
                        session,
                        user,
                        msgUid,
                        message,
                        hashCache);
            } catch (Exception e) {
                // log and do not fail the whole sync because of one invalid message
                logger.error("Persisting message " + msgUid + " in folder " + folder.getName() + " failed.", e);
            }
        }

		return newMessages;
	}

	private Map<Long, Message> getMessageUids(IMAPFolder folder, SyncUpdateMethod updateMode) throws MessagingException {
		Message[] messages;

		long time = System.currentTimeMillis();
		messages = folder.getMessages();
		if(logger.isDebugEnabled())
			logger.debug("IMAPFolder.getContacts() took " + (System.currentTimeMillis() - time) + "ms.");
	
		FetchProfile fp = new FetchProfile();
		fp.add(UIDFolder.FetchProfileItem.UID);
		if(isUpdateFlags(updateMode)) {
			logger.debug("Using Fetch Profile UID + FLAGS");
			fp.add(FetchProfileItem.FLAGS);
		}
		time = System.currentTimeMillis();
		folder.fetch(messages, fp);
		if(logger.isDebugEnabled())
			logger.debug("IMAPFolder.fetch() UIDs took " + (System.currentTimeMillis() - time) + "ms.");
	
		Map<Long, Message>uidMsgMap = new HashMap<Long, Message>();
		// TODO change everything to IMAP
		time = System.currentTimeMillis();
		for(int i=0; i<messages.length; i++)
			uidMsgMap.put(new Long(folder.getUID(messages[i])), messages[i]);
		if(logger.isDebugEnabled())
			logger.debug("Reading UIDs took " + (System.currentTimeMillis() - time) + "ms.");
		
		return uidMsgMap;
	}

    private void handleDeletedMessages(final Collection<Long> deleteUids, final UserPO userPO, final TagLineagePO tagLineage) {
        final int deleteCount = persistenceHelper.deleteMessagesForTagLineage(userPO.getId(), deleteUids, tagLineage.getId());
        if(logger.isDebugEnabled() && deleteCount > 0)
            logger.debug("Deleted " + deleteCount + " messages from database that where removed from IMAP folder before.");
    }
}