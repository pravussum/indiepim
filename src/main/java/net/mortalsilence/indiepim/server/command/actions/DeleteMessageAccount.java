package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.BooleanResult;

public class DeleteMessageAccount extends AbstractSessionAwareAction<BooleanResult> {

	private Long accountId;

	public Long getAccountId() {
		return accountId;
	}

	public DeleteMessageAccount(Long accountId) {
		this.accountId = accountId;
	}

	public DeleteMessageAccount() {
	}
	
	
}
