package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.calendar.synchronisation.CalSynchroService;
import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.CreateOrUpdateCalendar;
import net.mortalsilence.indiepim.server.command.actions.CreateOrUpdateEvent;
import net.mortalsilence.indiepim.server.command.results.IdResult;
import net.mortalsilence.indiepim.server.dao.CalendarDAO;
import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.dao.UserDAO;
import net.mortalsilence.indiepim.server.domain.CalendarPO;
import net.mortalsilence.indiepim.server.domain.EventPO;
import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import net.mortalsilence.indiepim.server.dto.CalendarDTO;
import net.mortalsilence.indiepim.server.dto.EventDTO;
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
public class CreateOrUpdateEventHandler implements Command<CreateOrUpdateEvent, IdResult> {

    @Inject private UserDAO userDAO;
    @Inject private GenericDAO genericDAO;
    @Inject private CalendarDAO calendarDAO;
    @Inject private CalendarUtils calendarUtils;

    @Transactional
    @Override
    public IdResult execute(CreateOrUpdateEvent action) {

		final EventDTO eventDTO = action.getEventDTO();
		final Long userId  = ActionUtils.getUserId();
        final UserPO user = userDAO.getUser(userId);

        EventPO eventPO;
        if(eventDTO.id != null) {
            eventPO = calendarDAO.getEventByIdAndUser(eventDTO.id, userId);
            if(eventPO == null)
                throw new RuntimeException("Event with id " + eventDTO.id + " not found!");
        } else {
            eventPO = new EventPO();
            eventPO.setUser(user);
        }
        if(eventDTO.calendarId == null) {
            final CalendarPO defaultCalendar = calendarDAO.getDefaultCalendar(userId);
            if(defaultCalendar == null)
                throw new RuntimeException("Default calendar for user " + userId + " not found!");
            eventPO.setCalendar(defaultCalendar);
        }

        calendarUtils.mapEventDT2EventPO(eventDTO, eventPO);

        // TODO update CALDAV calendar

        if(eventDTO.id == null) {
            genericDAO.persist(eventPO);
        } else {
            eventPO = genericDAO.update(eventPO);
        }

		return new IdResult(eventPO.getId());
	}


    @Override
	public void rollback(CreateOrUpdateEvent arg0, IdResult arg1) {
		// TODO implement rollback
	}

}
