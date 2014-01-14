package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.BooleanResult;

public class StartAccountSynchronisation extends AbstractSessionAwareAction<BooleanResult> {

	private Long accountId;
	private Boolean full; 

	public StartAccountSynchronisation(final Long accountId, final Boolean full) {
		this.accountId = accountId;
		this.full = full != null ? full : Boolean.FALSE;
	}

	public Boolean getFull() {
		return full;
	}

	public Long getAccountId() {
		return accountId;
	}

	public StartAccountSynchronisation() {
	}
	
}
