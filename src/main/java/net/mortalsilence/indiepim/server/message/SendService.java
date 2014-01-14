package net.mortalsilence.indiepim.server.message;

import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import net.mortalsilence.indiepim.server.exception.UserRuntimeException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

@Service
public class SendService implements MessageConstants {

    @Inject
    private MessageDAO messageDAO;
    @Inject
    private ConnectionUtils connectionUtils;

	public Long sendMessage( 	final Long userId,
								final Long accountId,
								final String subject,
								final List<String> to,
								final List<String> cc,
								final List<String> bcc,
								final String content,
								final boolean isHtml
								) {
        // TODO argument validation !!!

        if(to == null || to.isEmpty())
            throw new UserRuntimeException("No recipients given. Message is not sent.");

		final MessageAccountPO account = messageDAO.getMessageAccount(userId, accountId);

		final String protocol = connectionUtils.getOutgoingProtocol(account);
		final Session outgoingSession = connectionUtils.getSession(account, false);
        final Session incomingSession = connectionUtils.getSession(account, true);
		final Transport transport = connectionUtils.getTransport(account, outgoingSession, protocol);
        final Store store = connectionUtils.connectToStore(account, incomingSession);

		MimeMessage message = new MimeMessage(outgoingSession);
		try {
			message.setSentDate(Calendar.getInstance().getTime());
			message.setHeader("X-Mailer", "IndiePIM");
			message.setHeader("MIME-Version" , "1.0" );
			
			message.setFrom(new InternetAddress(account.getEmail()));
			Iterator<String> it = to.iterator();
            while(it.hasNext()) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(it.next()));
            }
            if(cc != null) {
                it = cc.iterator();
                while(it.hasNext()) {
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(it.next()));
                }
            }
            if(bcc != null) {
                it = bcc.iterator();
                while(it.hasNext()) {
                    message.addRecipient(Message.RecipientType.BCC, new InternetAddress(it.next()));
                }
            }

			message.setSubject(subject, "utf-8");
			if(isHtml) {				
				message.setContent(content, CONTENT_TYPE_TEXT_HTML_UTF8);
			} else {
				message.setContent( message, CONTENT_TYPE_TEXT_PLAIN_UTF8);
			}
			
			transport.sendMessage(message, message.getAllRecipients());
            // TODO make configurable
            final Folder folder = store.getFolder("INBOX.Sent");
            folder.open(Folder.READ_WRITE);
            message.setFlag(Flags.Flag.SEEN, true);
            folder.appendMessages(new Message[] {message});

            store.close();
			
		} catch (AddressException e) {
			e.printStackTrace();
			throw new RuntimeException("Address error (from " + account.getEmail() + " to " + to + ")." + e.getMessage());
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException("Error sending message (" + e.getMessage() + ")");
		} finally {
			if(transport != null) {
				try {
					transport.close();
				} catch (MessagingException e) {
					e.printStackTrace();
					// Ignore
				}
			} 
		}
		

		// TODO return message ID (IMAP or saved to DB?)
		return 0L;
	}


	
}
