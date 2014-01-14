package net.mortalsilence.indiepim.server.command.results;

import net.mortalsilence.indiepim.server.command.Result;
import net.mortalsilence.indiepim.server.dto.MessageAccountDTO;

import java.util.List;

public class GetMessageAccountsResult implements Result {

	private List<MessageAccountDTO> accounts;

	public GetMessageAccountsResult() { }

	public GetMessageAccountsResult(List<MessageAccountDTO> accounts) {
		this.accounts = accounts;
	}

	public List<MessageAccountDTO> getAccounts() {
		return accounts;
	}
	
	
}
