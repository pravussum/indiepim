package net.mortalsilence.indiepim.server.calendar;

import java.util.Set;

public class RecurrenceService {

//        final PeriodList recurrenceSet = new PeriodList();
//
//        final DtStart start = (DtStart) getProperty(Property.DTSTART);
//        DateProperty end = (DateProperty) getProperty(Property.DTEND);
//        if (end == null) {
//            end = (DateProperty) getProperty(Property.DUE);
//        }
//        Duration duration = (Duration) getProperty(Property.DURATION);
//
//        // if no start date specified return empty list..
//        if (start == null) {
//            return recurrenceSet;
//        }
//
//        final Value startValue = (Value) start.getParameter(Parameter.VALUE);
//
//        // initialise timezone..
////        if (startValue == null || Value.DATE_TIME.equals(startValue)) {
//        if (start.isUtc()) {
//            recurrenceSet.setUtc(true);
//        }
//        else if (start.getDate() instanceof DateTime) {
//            recurrenceSet.setTimeZone(((DateTime) start.getDate()).getTimeZone());
//        }
//
//        // if an explicit event duration is not specified, derive a value for recurring
//        // periods from the end date..
//        Dur rDuration;
//        // if no end or duration specified, end date equals start date..
//        if (end == null && duration == null) {
//            rDuration = new Dur(start.getDate(), start.getDate());
//        }
//        else if (duration == null) {
//            rDuration = new Dur(start.getDate(), end.getDate());
//        }
//        else {
//            rDuration = duration.getDuration();
//        }
//
//        // add recurrence dates..
//        for (final Iterator i = getProperties(Property.RDATE).iterator(); i.hasNext();) {
//            final RDate rdate = (RDate) i.next();
//            final Value rdateValue = (Value) rdate.getParameter(Parameter.VALUE);
//            if (Value.PERIOD.equals(rdateValue)) {
//                for (final Iterator j = rdate.getPeriods().iterator(); j.hasNext();) {
//                    final Period rdatePeriod = (Period) j.next();
//                    if (period.intersects(rdatePeriod)) {
//                        recurrenceSet.add(rdatePeriod);
//                    }
//                }
//            }
//            else if (Value.DATE_TIME.equals(rdateValue)) {
//                for (final Iterator j = rdate.getDates().iterator(); j.hasNext();) {
//                    final DateTime rdateTime = (DateTime) j.next();
//                    if (period.includes(rdateTime)) {
//                        recurrenceSet.add(new Period(rdateTime, rDuration));
//                    }
//                }
//            }
//            else {
//                for (final Iterator j = rdate.getDates().iterator(); j.hasNext();) {
//                    final Date rdateDate = (Date) j.next();
//                    if (period.includes(rdateDate)) {
//                        recurrenceSet.add(new Period(new DateTime(rdateDate), rDuration));
//                    }
//                }
//            }
//        }
//
//        // allow for recurrence rules that start prior to the specified period
//        // but still intersect with it..
//        final DateTime startMinusDuration = new DateTime(period.getStart());
//        startMinusDuration.setTime(rDuration.negate().getTime(
//                period.getStart()).getTime());
//
//        // add recurrence rules..
//        for (final Iterator i = getProperties(Property.RRULE).iterator(); i.hasNext();) {
//            final RRule rrule = (RRule) i.next();
//            final DateList rruleDates = rrule.getRecur().getDates(start.getDate(),
//                    new Period(startMinusDuration, period.getEnd()), startValue);
//            for (final Iterator j = rruleDates.iterator(); j.hasNext();) {
//                final Date rruleDate = (Date) j.next();
//                recurrenceSet.add(new Period(new DateTime(rruleDate), rDuration));
//            }
//        }
//
//        // add initial instance if intersection with the specified period..
//        Period startPeriod = null;
//        if (end != null) {
//            startPeriod = new Period(new DateTime(start.getDate()),
//                    new DateTime(end.getDate()));
//        }
//        else {
//            /*
//             * PeS: Anniversary type has no DTEND nor DUR, define DUR
//             * locally, otherwise we get NPE
//             */
//            if (duration == null) {
//                duration = new Duration(rDuration);
//            }
//
//            startPeriod = new Period(new DateTime(start.getDate()),
//                    duration.getDuration());
//        }
//        if (period.intersects(startPeriod)) {
//            recurrenceSet.add(startPeriod);
//        }
//
//        // subtract exception dates..
//        for (final Iterator i = getProperties(Property.EXDATE).iterator(); i.hasNext();) {
//            final ExDate exdate = (ExDate) i.next();
//            for (final Iterator j = recurrenceSet.iterator(); j.hasNext();) {
//                final Period recurrence = (Period) j.next();
//                // for DATE-TIME instances check for DATE-based exclusions also..
//                if (exdate.getDates().contains(recurrence.getStart())
//                        || exdate.getDates().contains(new Date(recurrence.getStart()))) {
//                    j.remove();
//                }
//            }
//        }
//
//        // subtract exception rules..
//        for (final Iterator i = getProperties(Property.EXRULE).iterator(); i.hasNext();) {
//            final ExRule exrule = (ExRule) i.next();
//            final DateList exruleDates = exrule.getRecur().getDates(start.getDate(),
//                    period, startValue);
//            for (final Iterator j = recurrenceSet.iterator(); j.hasNext();) {
//                final Period recurrence = (Period) j.next();
//                // for DATE-TIME instances check for DATE-based exclusions also..
//                if (exruleDates.contains(recurrence.getStart())
//                        || exruleDates.contains(new Date(recurrence.getStart()))) {
//                    j.remove();
//                }
//            }
//        }
//
//        return recurrenceSet;

}
