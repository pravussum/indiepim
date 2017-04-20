package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.BooleanResult;

public class DeleteCalendar extends AbstractSessionAwareAction<BooleanResult> {

	private Long calendarId;

	public Long getCalendarId() {
		return calendarId;
	}

	public DeleteCalendar(Long calendarId) {
		this.calendarId = calendarId;
	}

	public DeleteCalendar() {
	}
	
	
}
