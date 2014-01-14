package net.mortalsilence.indiepim.server.command.results;

import net.mortalsilence.indiepim.server.command.Result;
import net.mortalsilence.indiepim.server.dto.MessageDTO;

import java.util.List;

public class MessageDTOListResult implements Result {

	private List<MessageDTO> messages;

	public MessageDTOListResult() {

	}

	public MessageDTOListResult(List<MessageDTO> messages) {
		super();
		this.messages = messages;
	}

	public List<MessageDTO> getMessages() {
		return messages;
	}
	
}
