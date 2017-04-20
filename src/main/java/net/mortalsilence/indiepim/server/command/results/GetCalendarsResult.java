package net.mortalsilence.indiepim.server.command.results;

import net.mortalsilence.indiepim.server.command.Result;
import net.mortalsilence.indiepim.server.dto.CalendarDTO;
import net.mortalsilence.indiepim.server.dto.MessageAccountDTO;

import java.util.List;

public class GetCalendarsResult implements Result {

	private List<CalendarDTO> calendars;

	public GetCalendarsResult() { }

	public GetCalendarsResult(List<CalendarDTO> calendars) {
		this.calendars = calendars;
	}

	public List<CalendarDTO> getCalendars() {
		return calendars;
	}
	
	
}
