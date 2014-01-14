package net.mortalsilence.indiepim.server.google.contacts.model;

import com.google.api.client.util.Key;

import java.util.Iterator;
import java.util.List;

public class GoogleContact {

	@Key("id")
	public String id;
	
	@Key("title")
	public String title;
	
	@Key("gContact:userDefinedField")
    public List<GoogleContactUserDefinedField> userDefinedFields;

    @Key("gd:name")
    public GoogleContactName name;

    @Key("gd:phoneNumber")
    public List<GoogleContactPhoneNumber> phoneNumbers;

    @Key("gd:postalAddress")
    public List<GoogleContactSimpleAddress> addresses;

    @Key("gd:structuredPostalAddress")
    public List<GoogleContactStructuredAddress> structuredAddresses;

    @Key("gd:email")
    public List<GoogleContactEmailAddress> emailAddresses;

    @Key("gd:im")
    public List<GoogleContactIMAddress> imAddresses;

    @Key("gd:deleted")
    public Object deletedMarker;

    public Boolean isDeleted() {
        return deletedMarker != null;
    }

    @Key("link")
    public List<GoogleContactLink> links;

    public String getPhotoUrl() {

        final Iterator<GoogleContactLink> it = links.iterator();
        while(it.hasNext()) {
            final GoogleContactLink link = it.next();
            if(link.isPhotoLink() && link.etag != null) {
                return link.url;
            }
        }
        return null;
    }


}
