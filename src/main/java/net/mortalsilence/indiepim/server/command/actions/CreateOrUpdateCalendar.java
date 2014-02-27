package net.mortalsilence.indiepim.server.command.actions;

import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.IdResult;
import net.mortalsilence.indiepim.server.dto.CalendarDTO;
import net.mortalsilence.indiepim.server.dto.MessageAccountDTO;

import java.util.Calendar;

public class CreateOrUpdateCalendar extends AbstractSessionAwareAction<IdResult> {

	private CalendarDTO calendarDTO;

	public CalendarDTO getCalendarDTO() {
		return calendarDTO;
	}

	public CreateOrUpdateCalendar(CalendarDTO calendarDTO) {
		this.calendarDTO = calendarDTO;
	}

	public CreateOrUpdateCalendar() {
	}
	
}
