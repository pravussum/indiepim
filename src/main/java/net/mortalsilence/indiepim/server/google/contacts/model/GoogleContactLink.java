package net.mortalsilence.indiepim.server.google.contacts.model;

import com.google.api.client.util.Key;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 11.11.12
 * Time: 23:21
 * To change this template use File | Settings | File Templates.
 */
public class GoogleContactLink {

    protected static final String LINK_REL_PHOTO = "http://schemas.google.com/contacts/2008/rel#photo";

    @Key("@rel")
    public String type;

    @Key("@href")
    public String url;

    @Key("@type")
    public String urlType;

    @Key("@gd:etag")
    public String etag;

    public Boolean isPhotoLink() {
        return type != null ? LINK_REL_PHOTO.equals(type) : false;
    }

}
