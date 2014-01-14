package net.mortalsilence.indiepim.server.calendar;

public class CalendarConstants {

    public static final String CARDDAV_CALENDAR_DISPLAY_NAME = "X-WR-CALNAME";
    public static final String CARDDAV_VEVENT_RECURRENCE_RULE = "RRULE";
    public static final String CARDDAV_RRULE_FREQ = "FREQ";
    public static final String CARDDAV_RRULE_UNTIL = "UNTIL";
    public static final String CARDDAV_RRULE_COUNT = "COUNT";
    public static final String CARDDAV_RRULE_BYSECOND = "BYSECOND";
    public static final String CARDDAV_RRULE_BYMINUTE = "BYMINUTE";
    public static final String CARDDAV_RRULE_BYHOUR = "BYHOUR";
    public static final String CARDDAV_RRULE_BYDAY = "BYDAY";
    public static final String CARDDAV_RRULE_BYMONTHDAY = "BYMONTHDAY";
    public static final String CARDDAV_RRULE_BYYEARDAY = "BYYEARDAY";
    public static final String CARDDAV_RRULE_BYWEEKNO = "BYWEEKNO";
    public static final String CARDDAV_RRULE_BYMONTH = "BYMONTH";
    public static final String CARDDAV_RRULE_BYSETPOS = "BYSETPOS";
    public static final String CARDDAV_RRULE_WEEK_START_DAY = "WKST";

    enum FREQ {
        SECONDLY,MINUTELY,HOURLY,DAILY,WEEKLY,MONTHLY,YEARLY
    }

}
