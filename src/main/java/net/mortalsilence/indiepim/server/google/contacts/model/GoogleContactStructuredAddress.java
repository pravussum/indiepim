package net.mortalsilence.indiepim.server.google.contacts.model;

import com.google.api.client.util.Key;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 11.11.12
 * Time: 13:20
 * To change this template use File | Settings | File Templates.
 */
public class GoogleContactStructuredAddress extends GoogleContactAddress {

    @Key("@label")
    public String label;

    @Key("@rel")
    public String type;

    @Key("@primary")
    public Boolean isPrimary;

    @Key("gd:street")
    public String street;

    @Key("gd:pobox")
    public String poBox;

    @Key("gd:city")
    public String city;

    @Key("gd:region")
    public String region;

    @Key("gd:postcode")
    public String postCode;

    @Key("gd:country")
    public net.mortalsilence.indiepim.server.google.contacts.model.GoogleContactCountry country;

    @Key("gd:formattedAddress")
    public String formattedAddress;

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
