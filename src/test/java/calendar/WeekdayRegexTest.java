package calendar;

import junit.framework.Assert;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 15.02.14
 * Time: 22:29
 */
public class WeekdayRegexTest {

    @Test
    public void testWeekdayRegex() {
        final Pattern pattern = Pattern.compile("([+-]?[0-9]?)([A-Z]{2})");
        Matcher matcher = pattern.matcher("1MO");
        matcher.find();
        Assert.assertEquals(matcher.group(1), "1");
        Assert.assertEquals(matcher.group(2), "MO");
        matcher = pattern.matcher("MO");
        matcher.find();
        Assert.assertEquals(matcher.group(1), "");
        Assert.assertEquals(matcher.group(2), "MO");
        matcher = pattern.matcher("-1MO");
        matcher.find();
        Assert.assertEquals(matcher.group(1), "-1");
        Assert.assertEquals(matcher.group(2), "MO");
        matcher = pattern.matcher("+3MO");
        matcher.find();
        Assert.assertEquals(matcher.group(1), "+3");
        Assert.assertEquals(matcher.group(2), "MO");
    }
}
