package net.mortalsilence.indiepim.server.message;

import com.sun.mail.imap.IMAPFolder;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import net.mortalsilence.indiepim.server.domain.MessagePO;
import net.mortalsilence.indiepim.server.domain.MessageTagLineageMappingPO;
import net.mortalsilence.indiepim.server.utils.MessageUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Service
public class MessageUpdateService implements MessageConstants {
	
	final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");
    @Inject
    private MessageDAO messageDAO;
    @Inject
    private MessageUtils messageUtils;
    @Inject
    private ConnectionUtils connectionUtils;

    /**
     * Performs IMAP operations on a list of <i>MessagePO</i>s. This method iterates through the messages, finds its
     * corresponding taglineages (IMAP folders) for the account with id <i>accountId</i>. The IMAP folder is opened,
     * the corresponding determined and the <i>callback</i> is invoked with these parameters (messages and folder).
     * The parameter <i>expunge</i> indicates whether an expunge is to be performed on the IMAP folder on close,
     * thus if messages marked as deleted are to be permanently removed.
     * @param userId
     * @param messages
     * @param accountId
     * @param expunge
     * @param callback
     * @return
     */
    public List<MessagePO> updateImapMessages(final Long userId,
                                              final List<MessagePO> messages,
                                              final Long accountId,
                                              final boolean expunge,
                                              final ImapMsgOperationCallback callback) {

        // TODO rework this code. Sort by folder to avoid this many folder open and close operations

		final MessageAccountPO account = messageDAO.getMessageAccount(userId, accountId);
		/* only IMAP supported at the moment */
		if(account.getProtocol() == null || !PROTOCOL_IMAP.equals(account.getProtocol().toUpperCase())) {
			logger.error("Protocol " + account.getProtocol() + " not supported.");
			return null;
		}
		final List<MessagePO> resultList = new LinkedList<MessagePO>();
		final Session session = connectionUtils.getSession(account, true);
		final Store store = connectionUtils.connectToStore(account, session);
		char separator;
		Folder folder = null;
		try {
			separator = store.getDefaultFolder().getSeparator();
			for(final MessagePO msg : messages) {
				final Iterator<MessageTagLineageMappingPO> it = msg.getMsgTagLineageMappings().iterator(); 

				while(it.hasNext()) {
                    try {
                        final MessageTagLineageMappingPO mapping = it.next();
                        final String path = connectionUtils.getFolderPathFromTagLineage(separator, mapping.getTagLineage());
                        folder = store.getFolder(path);
                        if(!folder.exists()) /* Fix for [Google Mail] folder issue */
                            continue;
                        folder.open(Folder.READ_WRITE);
                        final Long msgUid = mapping.getMsgUid();
                        final Message imapMsg = messageUtils.getMsgByUID(folder, msgUid);
                        if(imapMsg == null) {
                            logger.error("updateImapMessages(): Msg with uid "+ msgUid +" not found on server. Account " + account.getId() + ", Folder " + folder.getFullName());
                        } else {
                            callback.processMessage((IMAPFolder)folder, imapMsg, msgUid, msg, mapping);
                        }
                        folder.close(expunge);
                    } finally {
                        if(folder != null && folder.isOpen())
                            folder.close(expunge);
                    }
                }
			}
			return resultList;
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if(store != null) {
				try {
					store.close();
				} catch (MessagingException e) {
					e.printStackTrace();
					/* Ignore */
				}
			}
		}
	}
}
