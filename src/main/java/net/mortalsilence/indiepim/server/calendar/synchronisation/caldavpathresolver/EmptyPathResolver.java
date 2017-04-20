package net.mortalsilence.indiepim.server.calendar.synchronisation.caldavpathresolver;

import net.fortuna.ical4j.connector.dav.PathResolver;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 18.02.14
 * Time: 20:46
 */
public class EmptyPathResolver extends PathResolver {

    @Override
    public String getUserPath(String username) {
        return "";
    }

    @Override
    public String getPrincipalPath(String username) {
        return "";
    }
}
