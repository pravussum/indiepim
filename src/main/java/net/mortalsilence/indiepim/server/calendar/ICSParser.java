package net.mortalsilence.indiepim.server.calendar;

import com.google.ical.iter.RecurrenceIterator;
import com.google.ical.iter.RecurrenceIteratorFactory;
import com.google.ical.values.DateValue;
import com.google.ical.values.DateValueImpl;
import com.google.ical.values.RRule;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.mortalsilence.indiepim.server.calendar.googlerfc2445adapter.RRuleAdapter;
import net.mortalsilence.indiepim.server.dao.CalendarDAO;
import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.domain.CalendarPO;
import net.mortalsilence.indiepim.server.domain.EventPO;
import net.mortalsilence.indiepim.server.domain.RecurrencePO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.MutableDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.*;

@Service
public class ICSParser {
    @Inject private GenericDAO genericDAO;
    @Inject private CalendarDAO calendarDAO;

    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");

    @Transactional
    public CalendarPO updateCalFromIcalInputStream(final UserPO user, CalendarPO indieCal, final InputStream is, final String name) {

        try {
            final CalendarBuilder builder = new CalendarBuilder();
            final Calendar calendar = builder.build(is);

            return updateCalFromIcalCalendar(user, indieCal, name, null, new Calendar[] {calendar});

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public CalendarPO updateCalFromIcalCalendar(final UserPO user,
                                                CalendarPO indieCal,
                                                final String name,
                                                final String color,
                                                final Calendar[] calendars) {

        if(indieCal == null) {
            indieCal = new CalendarPO();
            indieCal.setUser(user);
            indieCal.setName(name);
        }

        if(indieCal.getId() == null) {
            genericDAO.persist(indieCal);
        }

        if(color != null)
            indieCal.setColor(color);


        for(int c=0; c<calendars.length; c++) {
            final Calendar curCalDavCal = calendars[c];
            final Iterator<VEvent> it = curCalDavCal.getComponents(Component.VEVENT).iterator();
            while (it.hasNext())
            {
                final VEvent icsEvent = it.next();
                EventPO event = calendarDAO.getEventByUidAndUser(icsEvent.getUid().getValue(), user.getId());
                final Property etagProperty = icsEvent.getProperty(DavPropertyName.GETETAG.getName());
                final String etag = etagProperty != null ? etagProperty.getValue() : null;
                logger.debug("processing event with etag " + etag);

                if(event == null) {
                    event = new EventPO();
                    event.setUid(icsEvent.getUid().getValue());
                    event.setUser(user);
                } else {
                    if(event.getEtag() != null && event.getEtag().equals(etag)) {
                        logger.debug("Etag didn't change, not updating event with etag " + etag);
                        break; // nothing changed, don't touch the event
                    }
                }
                event.setEtag(etag);

                event.setName(icsEvent.getSummary() != null ? icsEvent.getSummary().getValue() : "<No Title>");
                event.setStart(icsEvent.getStartDate().getDate().getTime());
                event.setEnd(icsEvent.getEndDate().getDate().getTime());
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
                    final RecurrencePO recurrencePO = getRecurrencePOFromPropertyMap(user, rrulePropMap);

                    // TODO check if the recurrence needs to be updated
                    // a recurrence changed, when any of its attributes changed or when the original events
                    // parameters changed
                    if(event.getRecurrence() != null) {
                        genericDAO.remove(event.getRecurrence());
                        calendarDAO.deleteEventOccurences(user.getId(), event.getId());
                    }
                    genericDAO.persist(recurrencePO);
                    event.setRecurrence(recurrencePO);
                }
                if(event.getId() == null) {
                    event = genericDAO.merge(event);
                    logger.debug("Updated event with id " + event.getId());
                }
                else  {
                    genericDAO.persist(event);
                    logger.debug("Persisted event with id " + event.getId());
                }
                if(event.getRecurrence() != null) {
                    generateRecurrenceEvents(user, event, indieCal);
                }

            }
        }
        return indieCal;
    }

    private RecurrencePO getRecurrencePOFromPropertyMap(UserPO user, Map<String, String> rrulePropMap) {
        final RecurrencePO recurrencePO = new RecurrencePO();
        recurrencePO.setUser(user);
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
        return recurrencePO;
    }

    private void generateRecurrenceEvents(UserPO user, EventPO origEvent, CalendarPO indieCal) {
        final RecurrencePO recurrence = origEvent.getRecurrence();
        final RRule rRule = new RRuleAdapter(recurrence);
        final Duration duration = origEvent.getEnd() != null ? new Duration(origEvent.getEnd() - origEvent.getStart()) : null;

        DateTime eventStart = new DateTime(origEvent.getStart());
        final DateValue dtStart = new DateValueImpl(eventStart.getYear(), eventStart.getMonthOfYear(), eventStart.getDayOfMonth());
        final RecurrenceIterator it = RecurrenceIteratorFactory.createRecurrenceIterator(rRule, dtStart, TimeZone.getDefault());

        // generate event recurrences up to 2 years from now on
        // TODO: how to update?
        final DateTime twoYearsAfter = eventStart.plusYears(2);
        while(it.hasNext()) {
            final DateValue dateValue = it.next();
            final MutableDateTime jodaDate = new MutableDateTime(dateValue.year(), dateValue.month(), dateValue.day(), 0, 0, 0, 0);
            if(jodaDate.isAfter(twoYearsAfter))
                break;

            final EventPO indieEvent = new EventPO();
            indieEvent.setUser(user);
            indieEvent.setCalendar(indieCal);
            indieEvent.setOrigEvent(origEvent);
            indieEvent.setName(origEvent.getName());
            jodaDate.setHourOfDay(eventStart.getHourOfDay());
            jodaDate.setMinuteOfHour(eventStart.getMinuteOfHour());
            indieEvent.setStart(jodaDate.getMillis());
            if(duration != null) {
                jodaDate.add(duration);
                indieEvent.setEnd(jodaDate.getMillis());
            }
            indieEvent.setUid(origEvent.getUid());
            indieEvent.setLocation(origEvent.getLocation());
            indieEvent.setUrl(origEvent.getUrl());
            indieEvent.setDescription(origEvent.getDescription());
            genericDAO.persist(indieEvent);
        }

    }


}

