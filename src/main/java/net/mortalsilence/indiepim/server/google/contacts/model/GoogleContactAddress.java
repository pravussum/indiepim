package net.mortalsilence.indiepim.server.google.contacts.model;

import com.google.api.client.util.Key;
import net.mortalsilence.indiepim.server.contact.ContactConstants;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 11.11.12
 * Time: 13:20
 * To change this template use File | Settings | File Templates.
 */
public class GoogleContactAddress implements ContactConstants {

    protected static final String ADDRESS_REL_HOME = "http://schemas.google.com/g/2005#home";
    protected static final String ADDRESS_REL_OTHER = "http://schemas.google.com/g/2005#other";
    protected static final String ADDRESS_REL_WORK = "http://schemas.google.com/g/2005#work";

    @Key("@label")
    public String label;

    @Key("@rel")
    public String type;

    @Key("@primary")
    public Boolean isPrimary;

    /**
     * Converts a Google phone number rel type to our phone number type
     */
    public ADDRESS_TYPE convertAddressType() {
        if(type == null)
            return ADDRESS_TYPE.OTHER;
        if(type.equals(ADDRESS_REL_HOME))
            return ADDRESS_TYPE.HOME;
        if(type.equals(ADDRESS_REL_WORK))
            return ADDRESS_TYPE.WORK;
        if(type.equals(ADDRESS_REL_OTHER))
            return ADDRESS_TYPE.OTHER;

        /* Defaults to OTHER */
        return ADDRESS_TYPE.OTHER;
    }
}
