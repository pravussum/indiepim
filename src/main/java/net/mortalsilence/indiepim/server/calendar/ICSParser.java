package net.mortalsilence.indiepim.server.calendar;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.dao.UserDAO;
import net.mortalsilence.indiepim.server.domain.CalendarPO;
import net.mortalsilence.indiepim.server.domain.EventPO;
import net.mortalsilence.indiepim.server.domain.RecurrencePO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.*;

@Service
public class ICSParser {
    @Inject private UserDAO userDAO;
    @Inject private GenericDAO genericDAO;

    @Transactional
    public CalendarPO persistCalendarFromICSFile(UserPO user, CommonsMultipartFile upload) {

        try {
            final CalendarPO indieCal = new CalendarPO();
            InputStream is = upload.getInputStream();
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(is);
            java.util.Calendar today = java.util.Calendar.getInstance();
            today.set(java.util.Calendar.HOUR_OF_DAY, 0);
            today.clear(java.util.Calendar.MINUTE);
            today.clear(java.util.Calendar.SECOND);

            indieCal.setUser(user);

            final Property nameProp = calendar.getProperty(CalendarConstants.CARDDAV_CALENDAR_DISPLAY_NAME);
            if(nameProp != null) {
                indieCal.setName(nameProp.getValue());
            } else {
                indieCal.setName(upload.getName());
            }

            // TODO: Calendar with this name existing?
            genericDAO.persist(indieCal);

            Collection<VEvent> icsEvents = calendar.getComponents(Component.VEVENT);
            final Iterator<VEvent> it = icsEvents.iterator();
            while (it.hasNext())
            {
                VEvent icsEvent = it.next();
                final EventPO event = new EventPO();
                event.setUid(icsEvent.getUid().getValue());
                event.setName(icsEvent.getSummary() != null ? icsEvent.getSummary().getValue() : "<No Title>");
                event.setStart(icsEvent.getStartDate().getDate().getTime());
                event.setEnd(icsEvent.getEndDate().getDate().getTime());
                event.setUser(user);
                event.setCalendar(indieCal);
                final Property rruleProp = icsEvent.getProperty(CalendarConstants.CARDDAV_VEVENT_RECURRENCE_RULE);
                final Map<String, String> rrulePropMap = new HashMap<String, String>();

                if(rruleProp != null) {
                    final String rruleStr = rruleProp.getValue();
                    String[] parts = StringUtils.split(rruleStr, ";");
                    for(int i=0; i<parts.length; i++) {
                        final String[] nameValue = StringUtils.split(parts[i], "=");
                        if(nameValue.length < 2)
                            continue;
                        if(nameValue[0] != null) {
                            rrulePropMap.put(nameValue[0], nameValue[1]);
                        }
                    }
                    final RecurrencePO recurrencePO = new RecurrencePO();
                    recurrencePO.setFrequency(rrulePropMap.get(CalendarConstants.CARDDAV_RRULE_FREQ));
                    final String untilStr = rrulePropMap.get(CalendarConstants.CARDDAV_RRULE_UNTIL);
                    if(untilStr != null)
                        recurrencePO.setUntil(new Long(untilStr));
                    final String countStr = rrulePropMap.get(CalendarConstants.CARDDAV_RRULE_COUNT);
                    if(countStr != null) {
                        recurrencePO.setCount(new Integer(countStr));
                    }
                    recurrencePO.setBySecond(rrulePropMap.get(CalendarConstants.CARDDAV_RRULE_BYSECOND));
                    recurrencePO.setByMinute(rrulePropMap.get(CalendarConstants.CARDDAV_RRULE_BYMINUTE));
                    recurrencePO.setByHour(rrulePropMap.get(CalendarConstants.CARDDAV_RRULE_BYHOUR));
                    recurrencePO.setByDay(rrulePropMap.get(CalendarConstants.CARDDAV_RRULE_BYDAY));
                    recurrencePO.setByMonthDay(rrulePropMap.get(CalendarConstants.CARDDAV_RRULE_BYMONTHDAY));
                    recurrencePO.setByYearDay(rrulePropMap.get(CalendarConstants.CARDDAV_RRULE_BYYEARDAY));
                    recurrencePO.setByWeekNo(rrulePropMap.get(CalendarConstants.CARDDAV_RRULE_BYWEEKNO));
                    recurrencePO.setByMonth(rrulePropMap.get(CalendarConstants.CARDDAV_RRULE_BYMONTH));
                    final String setPosStr = rrulePropMap.get(CalendarConstants.CARDDAV_RRULE_BYSETPOS);
                    if(setPosStr != null)
                        recurrencePO.setBySetPos(new Integer(setPosStr));
                    recurrencePO.setWeekStartDay(rrulePropMap.get(CalendarConstants.CARDDAV_RRULE_WEEK_START_DAY));

                    genericDAO.persist(recurrencePO);
                    event.setRecurrence(recurrencePO);
                }
                genericDAO.persist(event);
            }
            return indieCal;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

