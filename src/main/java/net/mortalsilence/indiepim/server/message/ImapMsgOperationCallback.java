package net.mortalsilence.indiepim.server.message;

import com.sun.mail.imap.IMAPFolder;
import net.mortalsilence.indiepim.server.domain.MessagePO;
import net.mortalsilence.indiepim.server.domain.MessageTagLineageMappingPO;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 24.01.14
 * Time: 23:13
 */
public interface ImapMsgOperationCallback {
    public MessagePO processMessage(final IMAPFolder folder, final Message imapMessage, Long messageUID, final MessagePO indieMessage, MessageTagLineageMappingPO msgTagLineageMapping) throws MessagingException;
}
