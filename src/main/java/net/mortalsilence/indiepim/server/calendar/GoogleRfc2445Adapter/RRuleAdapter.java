package net.mortalsilence.indiepim.server.calendar.googlerfc2445adapter;

import com.google.ical.values.*;
import net.mortalsilence.indiepim.server.domain.RecurrencePO;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 15.02.14
 * Time: 22:01
 */
public class RRuleAdapter extends RRule {

    final private RecurrencePO indieRRule;

    public RRuleAdapter(final RecurrencePO recurrencePO) {
        super();
        this.indieRRule = recurrencePO;
    }

    @Override
    public Frequency getFreq() {
        return Frequency.valueOf(indieRRule.getFrequency());
    }

    @Override
    public void setFreq(Frequency freq) {
        indieRRule.setFrequency(freq.name());
    }

    @Override
    public Weekday getWkSt() {
        if(indieRRule.getWeekStartDay() == null)
            return Weekday.MO;
        return Weekday.valueOf(indieRRule.getWeekStartDay());
    }

    @Override
    public void setWkSt(Weekday wkst) {
        indieRRule.setWeekStartDay(wkst.name());
    }

    @Override
    public DateValue getUntil() {

        if(indieRRule.getUntil() == null)
            return null;

        // TODO make Timezone save and use Joda time
        final Calendar date = Calendar.getInstance();
        date.setTimeInMillis(indieRRule.getUntil());
        return new DateValueImpl(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void setUntil(DateValue until) {
        final Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, until.year());
        date.set(Calendar.MONTH, until.month());
        date.set(Calendar.DAY_OF_MONTH, until.day());
        indieRRule.setUntil(date.getTimeInMillis());
    }

    @Override
    public int getCount() {
        if(indieRRule.getCount() == null)
            return 0;
        return indieRRule.getCount();
    }

    @Override
    public void setCount(int count) {
        indieRRule.setCount(count);
    }

    @Override
    public int getInterval() {
        if(indieRRule.getInterval() == null)
            return 1;
        return indieRRule.getInterval();
    }

    @Override
    public void setInterval(int interval) {
        indieRRule.setInterval(interval);
    }

    @Override
    public List<WeekdayNum> getByDay() {
        final List<WeekdayNum> result = new LinkedList<WeekdayNum>();
        if(indieRRule.getByDay() == null || indieRRule.getByDay().isEmpty()) {
            return result;
        }

        final Pattern pattern = Pattern.compile("([+-]?[0-9]?)([A-Z]{2})");
        for(final String curDay : indieRRule.getByDay().split(",")) {
            final Matcher matcher = pattern.matcher(curDay);
            if(!matcher.find())
                throw new IllegalArgumentException("Cannot parse BYDAY item: " + curDay);
            final String modifier = matcher.group(1);
            final Integer num = modifier.isEmpty()? 0 : Integer.valueOf(modifier);
            result.add(new WeekdayNum(num, Weekday.valueOf(matcher.group(2))));
        }
        return result;
    }

    @Override
    public void setByDay(List<WeekdayNum> byDay) {
        if(byDay == null || byDay.isEmpty()) {
            indieRRule.setByDay(null);
            return;
        }
        StringBuffer result = new StringBuffer("");
        final Iterator<WeekdayNum> it = byDay.iterator();
        while(it.hasNext()) {
            final WeekdayNum weekdayNum = it.next();
            if(weekdayNum.num < 0)
                result.append("-").append(weekdayNum.num);
            else if(weekdayNum.num > 0)
                result.append(weekdayNum.num);
            result.append(weekdayNum.wday.name());
            if(it.hasNext())
                result.append(",");
        }
        indieRRule.setByDay(result.toString());
    }

    @Override
    public int[] getByMonth() {
        if(indieRRule.getByMonth() == null)
            return new int[]{};
        final String[] monthNums = indieRRule.getByMonth().split(",");
        final int[] result = new int[monthNums.length];
        int i=0;
        for(String curMonthNum : monthNums) {
            result[i++] = Integer.valueOf(curMonthNum);
        }
        return result;
    }

    @Override
    public void setByMonth(int[] byMonth) {
        if(byMonth.length == 0) {
            indieRRule.setByMonth(null);
            return;
        }
        final StringBuffer result = new StringBuffer("");
        for(int i=0; i<byMonth.length; i++) {
            result.append(byMonth[i]);
            if(i<byMonth.length)
                result.append(",");
        }
        indieRRule.setByMonth(result.toString());
    }

    @Override
    public int[] getByMonthDay() {
        if(indieRRule.getByMonthDay() == null)
            return new int[]{};
        final String[] monthNums = indieRRule.getByMonthDay().split(",");
        final int[] result = new int[monthNums.length];
        int i=0;
        for(String curMonthNum : monthNums) {
            result[i++] = Integer.valueOf(curMonthNum);
        }
        return result;
    }

    @Override
    public void setByMonthDay(int[] byMonthDay) {
        if(byMonthDay.length == 0) {
            indieRRule.setByMonthDay(null);
            return;
        }
        final StringBuffer result = new StringBuffer("");
        for(int i=0; i< byMonthDay.length; i++) {
            result.append(byMonthDay[i]);
            if(i< byMonthDay.length)
                result.append(",");
        }
        indieRRule.setByMonthDay(result.toString());
    }

    @Override
    public int[] getByWeekNo() {
        if(indieRRule.getByWeekNo() == null)
            return new int[]{};
        final String[] weekNos = indieRRule.getByWeekNo().split(",");
        final int[] result = new int[weekNos.length];
        int i=0;
        for(String curWeekNo : weekNos) {
            result[i++] = Integer.valueOf(curWeekNo);
        }
        return result;
    }

    @Override
    public void setByWeekNo(int[] byWeekNo) {
        if(byWeekNo.length == 0) {
            indieRRule.setByWeekNo(null);
            return;
        }
        final StringBuffer result = new StringBuffer("");
        for(int i=0; i< byWeekNo.length; i++) {
            result.append(byWeekNo[i]);
            if(i< byWeekNo.length)
                result.append(",");
        }
        indieRRule.setByWeekNo(result.toString());
    }

    @Override
    public int[] getByYearDay() {
        if(indieRRule.getByYearDay() == null)
            return new int[]{};
        final String[] yearDays = indieRRule.getByYearDay().split(",");
        final int[] result = new int[yearDays.length];
        int i=0;
        for(String curYearDay : yearDays) {
            result[i++] = Integer.valueOf(curYearDay);
        }
        return result;
    }

    @Override
    public void setByYearDay(int[] byYearDay) {
        if(byYearDay.length == 0) {
            indieRRule.setByYearDay(null);
            return;
        }
        final StringBuffer result = new StringBuffer("");
        for(int i=0; i< byYearDay.length; i++) {
            result.append(byYearDay[i]);
            if(i< byYearDay.length)
                result.append(",");
        }
        indieRRule.setByYearDay(result.toString());
    }

    @Override
    public int[] getBySetPos() {
        if(indieRRule.getBySetPos() == null)
            return new int[]{};
        // TODO implement full support (list of setpos)
        final int[] result = new int[1];
        result[0] = Integer.valueOf(indieRRule.getBySetPos());
        return result;
    }

    @Override
    public void setBySetPos(int[] bySetPos) {
        if(bySetPos.length == 0) {
            indieRRule.setBySetPos(null);
            return;
        }
        indieRRule.setBySetPos(bySetPos[0]);
    }

    @Override
    public int[] getByHour() {
        if(indieRRule.getByHour() == null)
            return new int[]{};
        final String[] hours = indieRRule.getByHour().split(",");
        final int[] result = new int[hours.length];
        int i=0;
        for(String curHour : hours) {
            result[i++] = Integer.valueOf(curHour);
        }
        return result;
    }

    @Override
    public void setByHour(int[] byHour) {
        if(byHour.length == 0) {
            indieRRule.setByHour(null);
            return;
        }
        final StringBuffer result = new StringBuffer("");
        for(int i=0; i< byHour.length; i++) {
            result.append(byHour[i]);
            if(i< byHour.length)
                result.append(",");
        }
        indieRRule.setByHour(result.toString());
    }

    @Override
    public int[] getByMinute() {
        if(indieRRule.getByMinute() == null)
            return new int[]{};
        final String[] minutes = indieRRule.getByMinute().split(",");
        final int[] result = new int[minutes.length];
        int i=0;
        for(String curMinute : minutes) {
            result[i++] = Integer.valueOf(curMinute);
        }
        return result;
    }

    @Override
    public void setByMinute(int[] byMinute) {
        if(byMinute.length == 0) {
            indieRRule.setByMinute(null);
            return;
        }
        final StringBuffer result = new StringBuffer("");
        for(int i=0; i< byMinute.length; i++) {
            result.append(byMinute[i]);
            if(i< byMinute.length)
                result.append(",");
        }
        indieRRule.setByMinute(result.toString());
    }

    @Override
    public int[] getBySecond() {
        if(indieRRule.getBySecond() == null)
            return new int[]{};
        final String[] seconds = indieRRule.getBySecond().split(",");
        final int[] result = new int[seconds.length];
        int i=0;
        for(String curSecond : seconds) {
            result[i++] = Integer.valueOf(curSecond);
        }
        return result;
    }

    @Override
    public void setBySecond(int[] bySecond) {
        if(bySecond.length == 0) {
            indieRRule.setBySecond(null);
            return;
        }
        final StringBuffer result = new StringBuffer("");
        for(int i=0; i< bySecond.length; i++) {
            result.append(bySecond[i]);
            if(i< bySecond.length)
                result.append(",");
        }
        indieRRule.setBySecond(result.toString());
    }
}
