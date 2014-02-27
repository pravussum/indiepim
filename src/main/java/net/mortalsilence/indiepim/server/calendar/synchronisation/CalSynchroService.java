package net.mortalsilence.indiepim.server.calendar.synchronisation;

import net.fortuna.ical4j.connector.ObjectNotFoundException;
import net.fortuna.ical4j.connector.ObjectStoreException;
import net.fortuna.ical4j.connector.dav.CalDavCalendarCollection;
import net.fortuna.ical4j.connector.dav.CalDavCalendarStore;
import net.fortuna.ical4j.connector.dav.PathResolver;
import net.mortalsilence.indiepim.server.calendar.ICSParser;
import net.mortalsilence.indiepim.server.domain.CalendarPO;
import net.mortalsilence.indiepim.server.security.EncryptionService;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 16.02.14
 * Time: 20:31
 */
@Service
public class CalSynchroService {

    @Inject ICSParser icsParser;
    @Inject EncryptionService encryptionService;

    public void syncExternalCalendar(final CalendarPO cal) {
        if(cal.getSyncUrl() == null)
            return;
        CalDavCalendarStore store = null;
        try {
            store = connectToCalDavStore(cal);
            try {
                CalDavCalendarCollection calDavCalendar = store.getCollection(cal.getSyncCalendarId());
                if(calDavCalendar == null)
                    throw new RuntimeException("Calendar with id '" + cal.getSyncCalendarId() + "' not found.");
                final String color = cal.getColor() != null ? cal.getColor() : calDavCalendar.getColor();
                icsParser.updateCalFromIcalCalendar(cal.getUser(), cal, calDavCalendar.getDisplayName(), color, calDavCalendar.getEvents());
            } catch (ObjectStoreException e) {
                e.printStackTrace();
            } catch (ObjectNotFoundException e) {
                e.printStackTrace();
            }

        } catch (MalformedURLException me) {
            throw new RuntimeException(me);
        } catch(IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            if(store != null)
                store.disconnect();
        }
    }

    public String getExtCalDisplayName(final CalendarPO cal) {
        if(cal.getSyncPrincipalPath() == null)
            return null;
        CalDavCalendarStore store = null;
        try {
            store = connectToCalDavStore(cal);
            try {
                CalDavCalendarCollection calDavCalendar = store.getCollection(cal.getSyncCalendarId());
                return calDavCalendar.getDisplayName();
            } catch (ObjectStoreException e) {
                e.printStackTrace();
            } catch (ObjectNotFoundException e) {
                return null;
            }
        } catch (MalformedURLException me) {
            throw new RuntimeException(me);
        } catch(IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            if(store != null) store.disconnect();
        }

        return null;
    }

    private CalDavCalendarStore connectToCalDavStore(CalendarPO cal) throws MalformedURLException {
        final URL url = new URL(cal.getSyncPrincipalPath()); // calDavCalendarStore only uses the protocol, host and port part
        // TODO handle self signed certificates properly ... let user check and add to trust store

        final PathResolver.GenericPathResolver pathResolver = new PathResolver.GenericPathResolver();
        pathResolver.setPrincipalPath(url.getPath());
        final URL rootUrl = new URL(url.getProtocol() + "://" + url.getHost() + ":" + url.getPort()) ;
        final CalDavCalendarStore store = new CalDavCalendarStore("IndiePIM", rootUrl, pathResolver);
        try {
            if(cal.getSyncUserName() != null && cal.getSyncPassword() != null) {
                final String password = encryptionService.decypher(cal.getSyncPassword());
                store.connect(cal.getSyncUserName(), password.toCharArray());
            } else {
                store.connect();
            }

        } catch (ObjectStoreException e) {
            e.printStackTrace();
        }
        return store;
    }
}
