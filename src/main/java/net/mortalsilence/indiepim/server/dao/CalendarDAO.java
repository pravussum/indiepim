package net.mortalsilence.indiepim.server.dao;

import net.mortalsilence.indiepim.server.domain.CalendarPO;
import net.mortalsilence.indiepim.server.domain.EventPO;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 15.02.14
 * Time: 23:38
 */
@Named
public class CalendarDAO {

    @PersistenceContext
    private EntityManager em;
    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");

    public EventPO getEventByIdAndUser(final Long eventId, final Long userId) {
        return em.createQuery("from EventPO where id = ?1 and user.id = ?2", EventPO.class)
                .setParameter(1, eventId)
                .setParameter(2, userId)
                .getSingleResult();
    }



    public List<EventPO> getEvents(final Long userId, final Timestamp startTs, final Timestamp endTs) {
        return em.createQuery("from EventPO where user.id = :userId and startTs >= :startTs and startTs <= :endTs", EventPO.class)
            .setParameter("userId", userId)
            .setParameter("startTs", startTs)
            .setParameter("endTs", endTs)
                .getResultList();
    }

    public EventPO getEventByUidAndUser(final String uid, final Long userId) {
        try {
            return em.createQuery("from EventPO where uid = :uid and user.id = :userId and origEvent is null", EventPO.class)
                    .setParameter("uid", uid)
                    .setParameter("userId", userId)
                    .getSingleResult();

        } catch(NoResultException nre) {
            return null;
        }
    }

    public void deleteCalendar(final Long userId, final Long calendarId) {
        em.createQuery("delete from RecurrencePO where id in (select recurrence.id from EventPO where calendar.id = :calendarId) and user.id = :userId")
            .setParameter("userId", userId)
            .setParameter("calendarId", calendarId)
            .executeUpdate();
        em.createQuery("delete from EventPO where calendar.id = :calendarId and user.id = :userId")
            .setParameter("userId", userId)
            .setParameter("calendarId", calendarId)
            .executeUpdate();
        em.createQuery("delete from CalendarPO where id = :calendarId and user.id = :userId")
            .setParameter("userId", userId)
            .setParameter("calendarId", calendarId)
            .executeUpdate();
    }

    public int deleteEventOccurences(final Long userId, final Long eventId) {
        return em.createQuery("delete from EventPO where user.id = :userId and origEvent.id = :eventId")
            .setParameter("userId", userId)
            .setParameter("eventId", eventId)
            .executeUpdate();
    }

    public List<CalendarPO> getCalendars(final Long userId) {
        return em.createQuery("from CalendarPO where user.id = :userId")
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<CalendarPO> getCalendars(final Long userId, final Collection<Long> calendarIds) {
        return em.createQuery("from CalendarPO where user.id = :userId and id in :calendarIds")
                .setParameter("userId", userId)
                .setParameter("calendarIds", calendarIds)
                .getResultList();
    }


    public CalendarPO getCalendarById(final Long userId, final Long calendarId) {
        try {
            return em.createQuery("from CalendarPO where user.id = :userId and id = :calendarId", CalendarPO.class)
                .setParameter("userId", userId)
                .setParameter("calendarId", calendarId)
                .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public CalendarPO getDefaultCalendar(final Long userId) {
        try {
            return em.createQuery("from CalendarPO where user.id = :userId and defaultCalendar = true", CalendarPO.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public CalendarPO getCalendarByUrl(final Long userId, final String url) {
        try {
            return em.createQuery("from CalendarPO where user.id = :userId and syncUrl = :url", CalendarPO.class)
                    .setParameter("userId", userId)
                    .setParameter("url", url)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }
}
