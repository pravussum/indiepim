package net.mortalsilence.indiepim.server.calendar.googlerfc2445adapter;

import com.google.ical.values.DateValue;
import com.google.ical.values.DateValueImpl;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 16.02.14
 * Time: 08:36
 */
public class GRfc2445Utils {

    public static DateValue getDateValue(final Calendar date) {
        // TODO respect time zone
        return new DateValueImpl(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
    }

    public static Date getDate (final DateValue dateValue) {
        final Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, dateValue.year());
        cal.set(Calendar.MONTH, dateValue.month()-1);
        cal.set(Calendar.DAY_OF_MONTH, dateValue.day());
        return cal.getTime();
    }
}
