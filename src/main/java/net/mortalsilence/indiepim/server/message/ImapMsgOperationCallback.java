package net.mortalsilence.indiepim.server.message;

import net.mortalsilence.indiepim.server.domain.MessagePO;

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
    public void processMessage(final Folder folder, final Message imapMessage, Long messageUID, final MessagePO indieMessage) throws MessagingException;
}
