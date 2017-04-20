package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.GetCalendars;
import net.mortalsilence.indiepim.server.command.results.GetCalendarsResult;
import net.mortalsilence.indiepim.server.dao.CalendarDAO;
import net.mortalsilence.indiepim.server.domain.CalendarPO;
import net.mortalsilence.indiepim.server.utils.CalendarUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
public class GetCalendarsHandler implements Command<GetCalendars, GetCalendarsResult> {

    @Inject private CalendarDAO calendarDAO;
    @Inject private CalendarUtils calendarUtils;

    @Transactional(readOnly = true)
	@Override
    public GetCalendarsResult execute(GetCalendars action) {

        final List<CalendarPO> calendars;
        if(action.getCalendarIds() != null && !action.getCalendarIds().isEmpty()) {
            calendars = calendarDAO.getCalendars(ActionUtils.getUserId(), action.getCalendarIds());
        } else {
            calendars = calendarDAO.getCalendars(ActionUtils.getUserId());
        }
		return new GetCalendarsResult(calendarUtils.mapCalPO2CalDTOList(calendars));
	}

	@Override
	public void rollback(GetCalendars arg0, GetCalendarsResult arg1) {
		// get method, no use to roll back
	}
	

}
