package net.mortalsilence.indiepim.server.google.contacts.model;

import com.google.api.client.util.Key;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 11.11.12
 * Time: 13:37
 * To change this template use File | Settings | File Templates.
 */
public class GoogleContactCountry {

    @Key("@code")
    public String countryCode;

    @Key("text()")
    public String name;

}
