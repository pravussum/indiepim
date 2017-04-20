package net.mortalsilence.indiepim.server.calendar.synchronisation.caldavpathresolver;

import net.fortuna.ical4j.connector.dav.PathResolver;
import net.mortalsilence.indiepim.server.exception.NotImplementedException;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 18.02.14
 * Time: 20:40
 */
public class FullURLPathResolver extends PathResolver {

    private String fullURL;

    public FullURLPathResolver(String fullURL) {
        this.fullURL = fullURL;
    }

    @Override
    public String getUserPath(String username) {
        return fullURL;
    }

    @Override
    public String getPrincipalPath(String username) {
        throw new NotImplementedException("Not implemented.");
    }
}
