package net.mortalsilence.indiepim.server.command.actions;

import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.IdVersionResult;
import net.mortalsilence.indiepim.server.dto.MessageAccountDTO;

public class CreateOrUpdateMessageAccount extends AbstractSessionAwareAction<IdVersionResult> {

	private MessageAccountDTO account;

	public MessageAccountDTO getAccount() {
		return account;
	}

	public CreateOrUpdateMessageAccount(MessageAccountDTO account) {
		this.account = account;
	}

	public CreateOrUpdateMessageAccount() {
	}
	
}
