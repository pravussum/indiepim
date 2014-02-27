package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.DeleteCalendar;
import net.mortalsilence.indiepim.server.command.actions.DeleteMessageAccount;
import net.mortalsilence.indiepim.server.command.results.BooleanResult;
import net.mortalsilence.indiepim.server.dao.CalendarDAO;
import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

import static org.quartz.JobKey.jobKey;

@Named
public class DeleteCalendarHandler implements Command<DeleteCalendar, BooleanResult> {

    @Inject
    private CalendarDAO calendarDAO;
    @Inject
    private GenericDAO genericDAO;

	@Transactional
    @Override
    public BooleanResult execute(DeleteCalendar action) {

        calendarDAO.deleteCalendar(ActionUtils.getUserId(), action.getCalendarId());

		/* Delete Sync Job */
		// Schedule the job with the trigger
//		SchedulerFactory sf = new StdSchedulerFactory();
//		try {
//			Scheduler scheduler = sf.getScheduler();
////			scheduler.deleteJob(jobKey("AccountIncSyncroJob_" + account.getId(),  "user_" + account.getUser().getId()));
//		} catch (SchedulerException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
		// TODO: warning to user

		return new BooleanResult(true);
	}

	@Override
	public void rollback(DeleteCalendar arg0, BooleanResult arg1) {
		// TODO implement undo
	}

}
