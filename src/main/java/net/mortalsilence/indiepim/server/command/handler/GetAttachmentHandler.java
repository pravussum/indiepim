package net.mortalsilence.indiepim.server.command.handler;

import com.sun.mail.imap.IMAPFolder;
import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.GetAttachment;
import net.mortalsilence.indiepim.server.command.exception.CommandException;
import net.mortalsilence.indiepim.server.command.results.BooleanResult;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.domain.AttachmentPO;
import net.mortalsilence.indiepim.server.domain.MessagePO;
import net.mortalsilence.indiepim.server.domain.MessageTagLineageMappingPO;
import net.mortalsilence.indiepim.server.message.ImapMsgOperationCallback;
import net.mortalsilence.indiepim.server.message.MessageConstants;
import net.mortalsilence.indiepim.server.message.MessageUpdateService;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Named
public class GetAttachmentHandler implements Command<GetAttachment, BooleanResult>, MessageConstants {

    @Inject MessageUpdateService messageUpdateService;
    @Inject MessageDAO messageDAO;

	@Transactional (readOnly = true)
    @Override
    public BooleanResult execute(final GetAttachment action) throws CommandException {

        final Long userId = ActionUtils.getUserId();
        if(action.getAttachmentId() == null)
            throw new IllegalArgumentException("AttachmentId must be set.");
        final AttachmentPO attachment = messageDAO.getAttachmentById(action.getAttachmentId(), userId);
        if(!DISPOSITION_ATTACHMENT.equalsIgnoreCase(attachment.getDisposition())) {
            throw new RuntimeException("No attachment disposition");
        }
        if(attachment.getFilename() == null) {
            throw new RuntimeException("Filname not set");
        }
        final MessagePO msg = attachment.getMessage();
        // use the first best msg tag lineage mapping
        final MessageTagLineageMappingPO mapping = msg.getMsgTagLineageMappings().iterator().next();
        if(mapping == null)
            throw new RuntimeException("No message tag lineage mapping found for message " + msg.getId());
        final BooleanResult result = new BooleanResult(false);
        messageUpdateService.handleSingleMessage(   userId,
                                                    mapping,
                                                    msg.getMessageAccount().getId(),
                                                    false,
                                                    false,
                                                    new ImapMsgOperationCallback() {
                @Override
                public MessagePO processMessage(IMAPFolder folder,
                                                Message imapMessage,
                                                Long messageUID,
                                                MessagePO indieMessage,
                                                MessageTagLineageMappingPO msgTagLineageMapping) throws MessagingException {
                    try {
                        final InputStream is = getContentStreamByFilename(imapMessage, attachment.getFilename());
                        if(is != null) {
                            final OutputStream outputStream = action.getOutputStream();
                            try {
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while( 0 < ( bytesRead = is.read( buffer ) ) ) {
                                    outputStream.write(buffer, 0, bytesRead);
                                }
                                result.setResult(true);
                            } catch(IOException ioe) {
                                throw new RuntimeException(ioe);
                            } finally {
                                if ( null != is )
                                    try { is.close();  } catch (IOException ioe) { /* ignore */ }
                            }
                        }
                    } catch (IOException ioe) {
                        throw new RuntimeException(ioe);
                    } catch (MessagingException me) {
                        throw new RuntimeException(me);
                    }
                    return indieMessage;
                }
            }
        );

        return result;
    }

    private InputStream getContentStreamByFilename(final Part part, final String filename) throws IOException, MessagingException {
        if(filename.equals(part.getFileName()))
            return part.getInputStream();
        if (part.isMimeType(CONTENT_TYPE_MULTIPART_ALL)) {
                final Multipart multipart = (Multipart) part.getContent();
                for(int i=0; i < multipart.getCount(); i++){
                    final InputStream is = getContentStreamByFilename(multipart.getBodyPart(i), filename);
                    if(is != null) return is;
                }
        }
        return null;
    }

	@Override
	public void rollback(GetAttachment arg0, BooleanResult arg1) {
		// no use rolling back a getter
	}

}
