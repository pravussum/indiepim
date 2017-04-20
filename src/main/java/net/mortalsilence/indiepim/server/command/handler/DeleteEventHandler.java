package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.DeleteCalendar;
import net.mortalsilence.indiepim.server.command.actions.DeleteEvent;
import net.mortalsilence.indiepim.server.command.results.BooleanResult;
import net.mortalsilence.indiepim.server.dao.CalendarDAO;
import net.mortalsilence.indiepim.server.dao.GenericDAO;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class DeleteEventHandler implements Command<DeleteEvent, BooleanResult> {

    @Inject
    private CalendarDAO calendarDAO;

	@Transactional
    @Override
    public BooleanResult execute(DeleteEvent action) {

        calendarDAO.deleteEvent(ActionUtils.getUserId(), action.getEventId());
		return new BooleanResult(true);
	}

	@Override
	public void rollback(DeleteEvent arg0, BooleanResult arg1) {
		// TODO implement undo
	}

}
