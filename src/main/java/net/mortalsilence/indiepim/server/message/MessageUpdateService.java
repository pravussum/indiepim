package net.mortalsilence.indiepim.server.message;

import net.mortalsilence.indiepim.server.SharedConstants;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import net.mortalsilence.indiepim.server.domain.MessagePO;
import net.mortalsilence.indiepim.server.domain.MessageTagLineageMappingPO;
import net.mortalsilence.indiepim.server.domain.TagLineagePO;
import net.mortalsilence.indiepim.server.utils.MessageUtils;
import org.apache.commons.lang3.StringUtils;
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

	public List<MessagePO> setImapFlagForMessages(final Long userId, final List<MessagePO> messages, final Long accountId, final Flags.Flag flag, boolean set) {
		
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
					final MessageTagLineageMappingPO mapping = it.next();
					final TagLineagePO tagLineage = mapping.getTagLineage();
					final String[] parts = StringUtils.split(tagLineage.getLineage(),SharedConstants.TAG_LINEAGE_SEPARATOR.toString());
					final String path = StringUtils.join(parts, separator);
					folder = store.getFolder(path);
					if(!folder.exists()) /* Fix for [Google Mail] folder issue */
						continue;
					folder.open(Folder.READ_WRITE);
					final Message imapMsg = messageUtils.getMsgByUID(folder, mapping.getMsgUid());
					if(imapMsg == null) {
						logger.error("setImapFlagForMessages(): Msg with uid "+ mapping.getMsgUid() +" not found on server. Account " + account.getId() + ", Folder " + folder.getFullName());
					} else {
						imapMsg.setFlag(flag, set);
						resultList.add(msg);
					}
					folder.close(true);
				}
			}
			return resultList;
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			if(folder != null && folder.isOpen()) {
				try {
					folder.close(false);
				} catch (MessagingException e) {
					e.printStackTrace();
					/* Ignore */
				}
			}
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
