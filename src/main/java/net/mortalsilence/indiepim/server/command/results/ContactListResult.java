package net.mortalsilence.indiepim.server.command.results;

import net.mortalsilence.indiepim.server.command.Result;
import net.mortalsilence.indiepim.server.dto.ContactListDTO;

import java.util.List;

public class ContactListResult implements Result {

	private List<ContactListDTO> contacts;
	private Long totalCount;


	public ContactListResult() {
	}

	public ContactListResult(List<ContactListDTO> messages, Long totalCount) {
		this.contacts = messages;
		this.totalCount = totalCount;
	}

	public List<ContactListDTO> getContacts() {
		return contacts;
	}

	public Long getTotalCount() {
		return totalCount;
	}
	
	
}
