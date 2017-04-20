package net.mortalsilence.indiepim.server.message.synchronisation;

import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.domain.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.*;
import javax.mail.internet.InternetAddress;

@Named
public class MailAddressHandler implements IncomingMessageHandler {

    @Inject
    private MessageDAO messageDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
    public MessagePO handleMessage(final Message message,
                                   final MessagePO messageObj,
                                   final Long msgUid,
                                   final MessageAccountPO account,
                                   final Folder folder,
                                   final TagLineagePO tagLineage,
                                   final Session session, UserPO user) {

		Address[] fromAddresses;
		try {
			fromAddresses = message.getFrom();
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException("Error parsing sender address for message uid " + msgUid);
		}
		for(Address curAddress : fromAddresses) {
			if(!(curAddress instanceof InternetAddress))
				throw new RuntimeException("Address type not supported in this context: " + curAddress.getType());
			final InternetAddress inetAddr = (InternetAddress)curAddress;
			
			EmailAddressPO emailAddr = messageDAO.getOrCreateEmailAddress(user, inetAddr.getAddress());
			if(!messageObj.getEmailAddresses().contains(emailAddr)) {
				messageObj.getEmailAddresses().add(emailAddr);
			}
		}

        return messageObj;
	}

}
