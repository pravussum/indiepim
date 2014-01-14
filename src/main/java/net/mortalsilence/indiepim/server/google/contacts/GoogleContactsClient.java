package net.mortalsilence.indiepim.server.google.contacts;

import com.google.api.client.http.*;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.services.gdata.xml.GDataXmlClient;
import net.mortalsilence.indiepim.server.google.GoogleConstants;
import net.mortalsilence.indiepim.server.google.contacts.model.GoogleContact;
import net.mortalsilence.indiepim.server.google.contacts.model.GoogleContactGroup;
import net.mortalsilence.indiepim.server.google.contacts.model.GoogleContactsFeed;
import net.mortalsilence.indiepim.server.google.contacts.model.GoogleContactsGroupFeed;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class GoogleContactsClient extends GDataXmlClient {

    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");

	static final XmlNamespaceDictionary DICTIONARY = new XmlNamespaceDictionary()
	.set("", "http://www.w3.org/2005/Atom")
	//      .set("exif", "http://schemas.google.com/photos/exif/2007")
	.set("gd", "http://schemas.google.com/g/2005")
	//      .set("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#")
	//      .set("georss", "http://www.georss.org/georss")
	//      .set("gml", "http://www.opengis.net/gml")
	//      .set("gphoto", "http://schemas.google.com/photos/2007")
	//      .set("media", "http://search.yahoo.com/mrss/")
	//      .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/")
	.set("xml", "http://www.w3.org/XML/1998/namespace");


	public GoogleContactsClient(HttpRequestFactory requestFactory) {
		super("3.0", requestFactory, DICTIONARY);
		setApplicationName("IndiePIM");
	}
	
	public List<GoogleContactGroup> getAllContactGroups() {
		try {
			GoogleContactsGroupFeed feed = executeGet(new GenericUrl(GoogleConstants.GOOGLE_API_CONTACTS_GROUP_FULL), GoogleContactsGroupFeed.class);
			return feed.entries;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
	}
	
	public List<GoogleContact> getContacts4Group(String groupId) {
		try {
			String parameters = "?group=" + groupId;
			parameters += "&max-results=" + Integer.MAX_VALUE;			
			GoogleContactsFeed feed = executeGet(new GenericUrl(GoogleConstants.GOOGLE_API_CONTACTS_FULL + parameters), GoogleContactsFeed.class);
			return feed.entries;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    public byte[] getPhoto(String photoUrl) {
        try {
            HttpRequest request = getRequestFactory().buildGetRequest(new GenericUrl(photoUrl));
            try {
                HttpResponse response = execute(request);
                final InputStream is = response.getContent();
                // TODO try to save the stream directly to DB using JPA only...
                return IOUtils.toByteArray(is);
            } catch (HttpResponseException e) {
                /* Workaround for invalid Google contact photo URLs (etag on photo link is set for contacts without photo) */
                if(e.getStatusCode() == 404)
                    logger.warn("Photo URL " + photoUrl + " is invalid (404)");
                else {
                    logger.error("Unexspected Http error.", e);
                }
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
