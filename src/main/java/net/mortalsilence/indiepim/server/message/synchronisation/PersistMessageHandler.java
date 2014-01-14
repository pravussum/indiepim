package net.mortalsilence.indiepim.server.message.synchronisation;

import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.domain.*;
import net.mortalsilence.indiepim.server.message.MessageConstants;
import net.mortalsilence.indiepim.server.utils.MessageUtils;
import net.mortalsilence.indiepim.server.utils.StringHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.activation.UnsupportedDataTypeException;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

@Service
public class PersistMessageHandler implements IncomingMessageHandler, MessageConstants {
	
	final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");
    @Inject
    private GenericDAO genericDAO;
    @Inject
    private MessageUtils messageUtils;
	
	public PersistMessageHandler() {
		// TODO: rework
        System.getProperties().put("mail.mime.base64.ignoreerrors", "true");
		System.getProperties().put("mail.mime.ignoreunknownencoding", "true");
		System.getProperties().put("mail.mime.decodetext.strict", "false");
        System.getProperties().put("mail.mime.multipart.allowempty", "true");
	}

	@Override
	public void handleMessage(final Message message,
                              MessagePO msg,
                              final Long msgUid,
                              final MessageAccountPO account,
                              final Folder folder,
                              final TagLineagePO tagLineage,
                              Session session, UserPO user) {

		msg.setUser(user);
		msg.setMessageAccount(account);
		try {
			try {
				msg.setSubject(message.getSubject() != null ? MimeUtility.decodeText(message.getSubject()) : null);
			} catch (UnsupportedEncodingException e) {
				logger.debug("UnsupportEncodingExeption", e);
			}
			msg.setSize(message.getSize());
			msg.setDateReceived(message.getReceivedDate());
			
			/* Get the SEEN flag before the content is received, because the IMAP server will then set the SEEN flag */
			handleFlags(message, msg);
			
			handleContent(message, new Long(msgUid), msg, session);

			// TODO re-unset the SEEN flag if it was not set? SEEN flag is then updated when first shown via web client... For now working probably due to READONLY mode  
			
			handleAddresses(message, msg);

            msg.addTagLineage(tagLineage, msgUid);

			/* PERSIST */
			genericDAO.updateOrPersist(msg);
			
		} catch (MessagingException e) {
			/* Workaround for email server bugs */
			logger.error("Error occured parsing message " + msgUid, e);
			if(e.getMessage().equals("Unable to load BODYSTRUCTURE") && message instanceof MimeMessage) {
				try {
					MimeMessage copy = new MimeMessage((MimeMessage)message);
					handleMessage(copy, msg, msgUid, account, folder, tagLineage, session, user);
				} catch (MessagingException e1) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			} else {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			
		}
	}

	private void handleFlags(final Message message, MessagePO msg) throws MessagingException {
		msg.setRead(message.isSet(Flags.Flag.SEEN));
	}

	private void handleAddresses(final Message message, MessagePO msg)
			throws MessagingException {
		/* handle recipients */ 
		msg.setReceiver(messageUtils.getReceiverStr(message));
		
		/* handle sender(s) */
		String senderStr = messageUtils.getSenderStr(message);
		msg.setSender(senderStr);
	}

	private void handleContent(Message message, final Long msgUid, final MessagePO msg, Session session) throws MessagingException {
		/* remove all former attachments (for message updates) */
		msg.getAttachments().clear();
		message = rebuildFromSourceIfNecessary(message, msgUid, session);
		
		/* recursivly iterate through the message parts */
		handleMessagePartAlternative(msg, msgUid, 1, message);
	
	}

	private Message rebuildFromSourceIfNecessary(final Message message,
			final Long msgUid, Session session) throws MessagingException {
		/* This is to be able to parse invalid MIME messages, that get recognized as text/plain 
		 * by an overstrict IMAP server like Courier */
		try {
			final String content = (String)message.getContent();
			if(message.getContentType().equalsIgnoreCase(CONTENT_TYPE_TEXT_PLAIN) && content.indexOf(CONTENT_TYPE) > 0) {
				logger.info("Rebuilding message " + msgUid + " from original message source."); 
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				message.writeTo(baos);
				final MimeMessage newMsg = new MimeMessage(session, (InputStream)new ByteArrayInputStream(baos.toByteArray()));
				return newMsg;
			}
		} catch (IOException ioe) {
			/* never mind, was worth a try */ 
			logger.error("Error parsing message content for message uid: " + msgUid, ioe);
		} catch (ClassCastException cce) {
			/* Not a text/plain message -> ignore */
		}
		return message;
	}

    private int handleMessagePartAlternative(MessagePO msg, final Long msgUid, int partPosNo, Part part) throws MessagingException {
        final String contentType = part.getContentType();
        if(contentType == null) {
            throw new RuntimeException("No Content-Type");
        }
        /*
         * PLAIN TEXT OR HTML and text/html not already set by former message part --> save as attachment in this case
         */
        if((part.isMimeType(CONTENT_TYPE_TEXT_HTML) && msg.getContentHtml() == null)
        || (part.isMimeType(CONTENT_TYPE_TEXT_PLAIN) && msg.getContentText() == null)) {
            try {
                // TODO remove if it never fails - there were problems with some strange messages in the past
                if(part.getContent() instanceof Part)
                    throw new RuntimeException("Part????");
                final InputStream is = part.getInputStream();
                final String mimeCharset = new ContentType(part.getContentType()).getParameter(CONTENT_TYPE_PARAM_CHARSET);
                String javaCharset = MimeUtility.javaCharset(mimeCharset);
                if(javaCharset == null) {
                    javaCharset = Charset.defaultCharset().name();
                    logger.warn("No java charset for given MIME charset " + mimeCharset + " (message " + msgUid + ". Using default charset.");
                }
                // drop malformed Characters
                final Charset charset = Charset.forName(javaCharset);
                final CharsetDecoder decoder = charset.newDecoder();
                decoder.onMalformedInput(CodingErrorAction.IGNORE);
                decoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
                final Reader inputReader = new InputStreamReader(is, decoder);
                if(part.isMimeType(CONTENT_TYPE_TEXT_PLAIN)) {
                    msg.setContentText(IOUtils.toString(inputReader));
                } else if (part.isMimeType(CONTENT_TYPE_TEXT_HTML)) {
                    msg.setContentHtml(IOUtils.toString(inputReader));
                }
            } catch(IOException ioe) {
                throw new RuntimeException(ioe);
            }
            partPosNo++;
        /*
         * MULTIPART --> recursivly go through the parts
         */
        } else if (part.isMimeType(CONTENT_TYPE_MULTIPART_ALL)) {
            try {
                final Multipart multipart = (Multipart) part.getContent();
                for(int i=0; i < multipart.getCount(); i++){
                    partPosNo = handleMessagePartAlternative(msg, msgUid, partPosNo, multipart.getBodyPart(i));
                }
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        /*
         * everything else --> handle es attachment
         */
        } else {
            AttachmentPO attachment = createAttachment(msg, part);
            attachment.setPosNo(partPosNo);
            partPosNo++;
            // multipart --> recursive
            // part --> unwrap
            // images or other text or anything --> attachment
        }
        return partPosNo;
    }

	private AttachmentPO createAttachment(MessagePO msg, Part part)
			throws MessagingException {
		AttachmentPO attachment = new AttachmentPO();
		attachment.setMessage(msg);
		attachment.setUser(msg.getUser());
		attachment.setFilename(StringUtils.abbreviate(part.getFileName(), 300));
		attachment.setMimeType(StringUtils.abbreviate(part.getContentType(), 150));
		attachment.setDisposition(part.getDisposition());
		return attachment;
	}	

}
