package net.mortalsilence.indiepim.server.message.synchronisation;

import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import net.mortalsilence.indiepim.server.domain.MessagePO;
import net.mortalsilence.indiepim.server.domain.TagLineagePO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.*;

@Named
public class UpdateFlagsAndFoldersHandler implements IncomingMessageHandler {

    @Inject
    private GenericDAO genericDAO;

	@Override
    @Transactional(propagation = Propagation.REQUIRED)
	public MessagePO handleMessage(Message message,
                                   MessagePO messageObj,
                                   Long msgUid,
                                   MessageAccountPO account,
                                   Folder folder,
                                   TagLineagePO tagLineage,
                                   Session session, UserPO user) {
		
		try {
			messageObj.setRead(message.isSet(Flags.Flag.SEEN));
			/* PERSIST */
            messageObj = genericDAO.update(messageObj);
            return messageObj;
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

}
