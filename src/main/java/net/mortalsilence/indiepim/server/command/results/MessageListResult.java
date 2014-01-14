package net.mortalsilence.indiepim.server.command.results;

import net.mortalsilence.indiepim.server.command.Result;
import net.mortalsilence.indiepim.server.dto.MessageListDTO;

import java.util.List;

public class MessageListResult implements Result {

	private List<MessageListDTO> messages;
	private Long totalCount;

	
	public MessageListResult() {
	}

	public MessageListResult(List<MessageListDTO> messages, Long totalCount) {
		this.messages = messages;
		this.totalCount = totalCount;
	}

	public List<MessageListDTO> getMessages() {
		return messages;
	}

	public Long getTotalCount() {
		return totalCount;
	}
	
	
}
