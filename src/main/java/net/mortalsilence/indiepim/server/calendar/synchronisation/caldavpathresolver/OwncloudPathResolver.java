package net.mortalsilence.indiepim.server.calendar.synchronisation.caldavpathresolver;

import net.fortuna.ical4j.connector.dav.PathResolver;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 17.02.14
 * Time: 20:55
 */
public class OwncloudPathResolver extends PathResolver {

    @Override
    public String getUserPath(String username) {
        return "/remote.php/caldav/calendars/" + username + "/";
    }

    @Override
    public String getPrincipalPath(String username) {
        return "/remote.php/caldav/principals/" + username + "/";
    }
}
