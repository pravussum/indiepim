package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.MessageDTOResult;

public class GetMessage extends AbstractSessionAwareAction<MessageDTOResult> {

	private Long messageId;
	
	public GetMessage(Long messageId) {
		this.messageId = messageId;
	}

	public Long getMessageId() {
		return messageId;
	}

	public GetMessage() {
	}	
}
