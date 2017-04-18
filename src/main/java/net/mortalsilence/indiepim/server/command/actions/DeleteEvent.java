package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.BooleanResult;

public class DeleteEvent extends AbstractSessionAwareAction<BooleanResult> {

	private Long eventId;

	public Long getEventId() {
		return eventId;
	}

	public DeleteEvent(Long eventId) {
		this.eventId = eventId;
	}

	public DeleteEvent() {
	}
	
	
}
