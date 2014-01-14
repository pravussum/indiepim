package net.mortalsilence.indiepim.server.google.contacts.model;

import com.google.api.client.util.Key;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 11.11.12
 * Time: 23:05
 * To change this template use File | Settings | File Templates.
 */
public class GoogleContactName {

    @Key("gd:givenName")
    public net.mortalsilence.indiepim.server.google.contacts.model.GoogleContactGivenName givenName;

    @Key("gd:familyName")
    public net.mortalsilence.indiepim.server.google.contacts.model.GoogleContactFamilyName familyName;

    @Key("gd:familyName/text()")
    public String test;

    @Key("gd:fullName")
    public net.mortalsilence.indiepim.server.google.contacts.model.GoogleContactFullName fullName;

}
