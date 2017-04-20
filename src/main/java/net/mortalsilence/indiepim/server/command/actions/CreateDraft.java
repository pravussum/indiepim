package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.CreateDraftResult;
import net.mortalsilence.indiepim.server.command.results.MessageDTOResult;

public class CreateDraft extends AbstractSessionAwareAction<CreateDraftResult> {

	private Long origMessageId;

	public CreateDraft(Long origMessageId) {
		this.origMessageId = origMessageId;
	}

	public Long getOrigMessageId() {
		return origMessageId;
	}

	public CreateDraft() {
	}	
}
