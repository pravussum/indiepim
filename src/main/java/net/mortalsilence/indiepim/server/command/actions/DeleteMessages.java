package net.mortalsilence.indiepim.server.command.actions;

import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.BooleanResult;
import net.mortalsilence.indiepim.server.command.results.MessageDTOListResult;

import java.util.List;


public class DeleteMessages extends AbstractSessionAwareAction<BooleanResult> {

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
