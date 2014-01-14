package net.mortalsilence.indiepim.server.message.synchronisation;

import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import net.mortalsilence.indiepim.server.domain.MessagePO;
import net.mortalsilence.indiepim.server.domain.TagLineagePO;
import net.mortalsilence.indiepim.server.domain.UserPO;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;

public interface IncomingMessageHandler {

	public void handleMessage(Message message,
                              MessagePO messageObj,
                              Long msgUid,
                              MessageAccountPO account,
                              Folder folder,
                              TagLineagePO tagLineage,
                              Session session, UserPO user);
	
}
