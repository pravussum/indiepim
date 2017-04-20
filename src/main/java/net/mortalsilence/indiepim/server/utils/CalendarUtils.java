package net.mortalsilence.indiepim.server.utils;

import net.mortalsilence.indiepim.server.dao.CalendarDAO;
import net.mortalsilence.indiepim.server.domain.CalendarPO;
import net.mortalsilence.indiepim.server.domain.EventPO;
import net.mortalsilence.indiepim.server.dto.CalendarDTO;
import net.mortalsilence.indiepim.server.dto.EventDTO;
import net.mortalsilence.indiepim.server.security.EncryptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 16.02.14
 * Time: 21:16
 */
@Service
public class CalendarUtils {

    @Inject EncryptionService encryptionService;
    @Inject CalendarDAO calendarDAO;

    public List<CalendarDTO> mapCalPO2CalDTOList(final Collection<CalendarPO> calendars) {
        if(calendars == null)
            return null;
        final List<CalendarDTO> result = new LinkedList<CalendarDTO>();
        for(final CalendarPO calendar : calendars) {
            final CalendarDTO calDTO = mapCalPO2CalDTO(calendar);
            // don't provide the sync password to the client... can be overriden though
            result.add(calDTO);
        }
        return result;
    }

    public CalendarDTO mapCalPO2CalDTO(final CalendarPO calendar) {
        final CalendarDTO calDTO = new CalendarDTO();
        calDTO.id = calendar.getId();
        calDTO.color = calendar.getColor();
        calDTO.defaultCalendar = calendar.getDefaultCalendar();
        calDTO.name = calendar.getName();
        calDTO.syncUrl = calendar.getSyncUrl();
        calDTO.userName = calendar.getSyncUserName();
        calDTO.syncPrincipalPath = calendar.getSyncPrincipalPath();
        return calDTO;
    }

    public void mapCalDTO2CalPO(final CalendarDTO calendarDTO, final CalendarPO calendarPO) {
        calendarPO.setSyncUrl(calendarDTO.syncUrl);
        calendarPO.setSyncUserName(calendarDTO.userName);
        if(calendarDTO.password != null)
            calendarPO.setSyncPassword(encryptionService.cypherText(calendarDTO.password));
        calendarPO.setSyncPrincipalPath(calendarDTO.syncPrincipalPath);
        calendarPO.setColor(calendarDTO.color);
        calendarPO.setName(calendarDTO.name);
        calendarPO.setDefaultCalendar(calendarDTO.defaultCalendar);
    }

    public List<EventDTO> mapEventPO2EventDTOList(final Collection<EventPO> events) {
        if(events == null)
            return null;
        final List<EventDTO> result = new LinkedList<EventDTO>();
        if(events.isEmpty())
            return result;

        for(final EventPO event : events) {
            final EventDTO eventDTO = mapEventPO2EventDTO(event);
            eventDTO.color = event.getCalendar().getColor();
            result.add(eventDTO);
        }
        return result;
    }

    public EventDTO mapEventPO2EventDTO(final EventPO event) {
        final EventDTO eventDTO = new EventDTO();
        eventDTO.id = event.getId();
        eventDTO.allDay = event.getStartTs() == null || event.getEndTs() == null;
        eventDTO.start = event.getStartTs() != null ? event.getStartTs().getTime() : event.getStart();
        eventDTO.end = event.getEndTs() != null ? event.getEndTs().getTime() : event.getEnd();
        eventDTO.title = event.getName();
        eventDTO.url = event.getUrl();
        eventDTO.description = event.getDescription();
        eventDTO.location = event.getLocation();
        eventDTO.calendarId = event.getCalendar().getId();
        return eventDTO;
    }

    public void mapEventDT2EventPO(final EventDTO eventDTO, final EventPO eventPO) {
        eventPO.setStart(eventDTO.start);
        eventPO.setEnd(eventDTO.end);
        eventPO.setDescription(eventDTO.description);
        eventPO.setLocation(eventDTO.location);
        eventPO.setName(eventDTO.title);
        eventPO.setUrl(eventDTO.url);
        if(eventDTO.calendarId != null && (eventPO == null || !eventDTO.equals(eventPO.getCalendar().getId()))){
            eventPO.setCalendar(calendarDAO.getCalendarById(eventPO.getUser().getId(), eventDTO.calendarId));
        }
    }
}
