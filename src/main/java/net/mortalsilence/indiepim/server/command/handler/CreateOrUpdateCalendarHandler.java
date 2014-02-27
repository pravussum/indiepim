package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.calendar.synchronisation.CalSynchroService;
import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.CreateOrUpdateCalendar;
import net.mortalsilence.indiepim.server.command.results.IdResult;
import net.mortalsilence.indiepim.server.dao.*;
import net.mortalsilence.indiepim.server.domain.*;
import net.mortalsilence.indiepim.server.dto.CalendarDTO;
import net.mortalsilence.indiepim.server.schedule.AccountIncSyncroJob;
import net.mortalsilence.indiepim.server.utils.CalendarUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

import java.net.MalformedURLException;
import java.net.URL;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Named
public class CreateOrUpdateCalendarHandler implements Command<CreateOrUpdateCalendar, IdResult> {

    @Inject private UserDAO userDAO;
    @Inject private GenericDAO genericDAO;
    @Inject private Scheduler scheduler;
    @Inject private CalendarDAO calendarDAO;
    @Inject private CalendarUtils calendarUtils;
    @Inject private CalSynchroService calSynchroService;

    @Transactional
    @Override
    public IdResult execute(CreateOrUpdateCalendar action) {

		final CalendarDTO calendarDTO = action.getCalendarDTO();
		final Long userId  = ActionUtils.getUserId();
        final UserPO user = userDAO.getUser(userId);

        CalendarPO calendarPO;
		if(calendarDTO.id != null) {
			calendarPO = calendarDAO.getCalendarById(userId, calendarDTO.id);
			if(calendarPO == null)
				throw new RuntimeException("Calendar with id " + calendarDTO.id + " not found. Could not update.");
		} else {
			calendarPO = new CalendarPO();
			calendarPO.setUser(user);
		}
        final String fullSyncUrl = calendarDTO.syncUrl;
        final boolean syncUrlChanged = fullSyncUrl != null && !fullSyncUrl.equals(calendarPO.getSyncUrl());
        calendarUtils.mapCalDTO2CalPO(calendarDTO, calendarPO);
        // TODO autofill values with information from remote calendar (if syncurl present)

        if(syncUrlChanged) {
            calendarPO.setSyncUrl(fullSyncUrl);
            // Remote calendar, test connection and get display name
            final String[] parts = fullSyncUrl.split("/");
            if(parts.length < 2)
                throw new RuntimeException("Could not parse calendar url: '" + fullSyncUrl + "'.");
            final String calName;
            try {
                calName = new URL(fullSyncUrl).getPath();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            calendarPO.setSyncCalendarId(calName);
            // try different principal urls:
            // .
            calendarPO.setSyncPrincipalPath(StringUtils.join(parts, "/", 0, parts.length - 2));
            String displayName = calSynchroService.getExtCalDisplayName(calendarPO);
            if(displayName == null) {
                // ./users
                calendarPO.setSyncPrincipalPath(StringUtils.join(parts, "/", 0, parts.length - 2) + "/users/");
                displayName = calSynchroService.getExtCalDisplayName(calendarPO);
            }
            if(displayName == null) {
                // ../principals
                calendarPO.setSyncPrincipalPath(StringUtils.join(parts, "/", 0, parts.length - 3) + "/principals/");
                displayName = calSynchroService.getExtCalDisplayName(calendarPO);
            }
            if(displayName == null) {
                // ../principals/users
                calendarPO.setSyncPrincipalPath(StringUtils.join(parts, "/", 0, parts.length - 3) + "/principals/users");
                displayName = calSynchroService.getExtCalDisplayName(calendarPO);
            }
            // TODO add support for GCal - still working at all? path is...
            // "/calendar/dav/" + username + "/events/"  + calendar name ? for calendars and
            // "/calendar/dav/" + username + "/user/" as principal URL, so should be ../../user/


            if(displayName == null) {
                throw new RuntimeException("Could not determine principal URL from calendar URL: " + fullSyncUrl);
            }

            calendarPO.setName(displayName);
            calendarPO.setDefaultCalendar(false);

        }

		if(calendarDTO.id != null) {
			calendarPO = genericDAO.update(calendarPO);
		} else {
			genericDAO.persist(calendarPO);
		}

        // Start initial sync
        if(syncUrlChanged) {
            calSynchroService.syncExternalCalendar(calendarPO);
        }

        // TODO create / update sync job
//    	if(calendarPO.getSyncInterval() != null && calendarPO.getSyncInterval() > 0)
//			initSyncroJob(calendarPO, calendarPO.getSyncInterval());
//		else {
//			deleteSyncroJob(calendarPO);
//		}
		
		return new IdResult(calendarPO.getId());
	}

	private void initSyncroJob(final MessageAccountPO accountPO, final Integer syncInterval) {
		// Define job instance
		JobDetail newJob = newJob(AccountIncSyncroJob.class)
		    .withIdentity("AccountIncSyncroJob", "user_" + accountPO.getUser().getId() + "_account_" + accountPO.getId())
    		.usingJobData("userId", accountPO.getUser().getId())
			.usingJobData("accountId", accountPO.getId())
		    .build();
		    
		Trigger trigger = newTrigger()
		    .withIdentity("AccountIncSyncroTrigger", "user_" + accountPO.getUser().getId() + "_account_" + accountPO.getId())
		    .startAt(futureDate(syncInterval, IntervalUnit.MINUTE))
		    .withSchedule(simpleSchedule()
                    .withIntervalInMinutes(syncInterval)
                    .repeatForever()
            ).build();
			            
		deleteSyncroJob(accountPO);

		try {
			scheduler.scheduleJob(newJob, trigger);			
		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
	private void deleteSyncroJob(final MessageAccountPO accountPO) {
		try {
			scheduler.deleteJob(jobKey("AccountIncSyncroJob", "user_" + accountPO.getUser().getId() + "_account_" + accountPO.getId()));
		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}			
	}

    @Override
	public void rollback(CreateOrUpdateCalendar arg0, IdResult arg1) {
		// TODO implement rollback
	}

}
