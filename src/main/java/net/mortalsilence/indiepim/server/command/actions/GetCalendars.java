package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.GetCalendarsResult;

import java.util.Collection;

public class GetCalendars extends AbstractSessionAwareAction<GetCalendarsResult> {

    private Collection<Long> calendarIds;

	public GetCalendars() {
	}

    public GetCalendars(final Collection<Long> calendarIds) {
        this.calendarIds = calendarIds;
    }

    public Collection<Long> getCalendarIds() {
        return calendarIds;
    }

    public void setCalendarIds(Collection<Long> id) {
        this.calendarIds = id;
    }
}
