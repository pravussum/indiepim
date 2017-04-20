package net.mortalsilence.indiepim.server.command.actions;

import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.IdResult;
import net.mortalsilence.indiepim.server.dto.EventDTO;

public class CreateOrUpdateEvent extends AbstractSessionAwareAction<IdResult> {

	private EventDTO eventDTO;

	public EventDTO getEventDTO() {
		return eventDTO;
	}

	public CreateOrUpdateEvent(EventDTO eventDTO) {
		this.eventDTO = eventDTO;
	}

	public CreateOrUpdateEvent() {
	}
	
}
