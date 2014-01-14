package net.mortalsilence.indiepim.server.command.results;

import net.mortalsilence.indiepim.server.command.Result;
import net.mortalsilence.indiepim.server.dto.MessageDTO;

public class MessageDTOResult implements Result {

	private MessageDTO messageDTO;

	public MessageDTOResult() {
	}

	public MessageDTOResult(MessageDTO messageDTO) {
		this.messageDTO = messageDTO;
	}

	public MessageDTO getMessageDTO() {
		return messageDTO;
	}
	
}
