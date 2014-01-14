package net.mortalsilence.indiepim.server.command.actions;

import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.MessageDTOListResult;

import java.util.List;


public class MarkMessagesAsRead extends AbstractSessionAwareAction<MessageDTOListResult> {

	private List<Long> messageIds;
	private boolean read = true;

	public MarkMessagesAsRead() {
	}

	public MarkMessagesAsRead(List<Long> messageIds) {
		this.messageIds = messageIds;
	}

	public MarkMessagesAsRead(List<Long> messageIds, boolean read) {
		this.messageIds = messageIds;
		this.read = read;
	}
	
	public List<Long> getMessageIds() {
		return messageIds;
	}

	public boolean isRead() {
		return read;
	}
}
