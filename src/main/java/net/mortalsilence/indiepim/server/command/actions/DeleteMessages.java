package net.mortalsilence.indiepim.server.command.actions;

import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.DeleteMessagesResult;

import java.util.List;


public class DeleteMessages extends AbstractSessionAwareAction<DeleteMessagesResult> {

	private List<Long> messageIds;

	public DeleteMessages() {
	}

	public DeleteMessages(List<Long> messageIds) {
		this.messageIds = messageIds;
	}

	public List<Long> getMessageIds() {
		return messageIds;
	}
}
