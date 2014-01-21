package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.BooleanResult;

public class SendChatMessage extends AbstractSessionAwareAction<BooleanResult> {

	private Long userId;
	private String message;

	public SendChatMessage(final Long userId, final String message) {
		this.userId = userId;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public Long getUserId() {
		return userId;
	}

	public SendChatMessage() {
	}
	
}
