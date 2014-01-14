package net.mortalsilence.indiepim.server.google.contacts.model;

import com.google.api.client.util.Key;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 11.11.12
 * Time: 00:06
 * To change this template use File | Settings | File Templates.
 */
public class GoogleContactUserDefinedField {

    @Key("@key")
    public String key;

    @Key("@value")
    public String value;
}
