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
public class GoogleContactIMAddress implements ContactConstants {

//    protected static final String IM_REL_HOME = "http://schemas.google.com/g/2005#home";
//    protected static final String IM_REL_OTHER = "http://schemas.google.com/g/2005#other";
//    protected static final String IM_REL_WORK = "http://schemas.google.com/g/2005#work";
//    protected static final String IM_REL_NETMEETING = "http://schemas.google.com/g/2005#netmeeting";

    protected static final String IM_REL_AOL = "http://schemas.google.com/g/2005#AIM";
    protected static final String IM_REL_MSN = "http://schemas.google.com/g/2005#MSN";
    protected static final String IM_REL_YAHOO = "http://schemas.google.com/g/2005#YAHOO";
    protected static final String IM_REL_SKYPE = "http://schemas.google.com/g/2005#SKYPE";
    protected static final String IM_REL_QQ = "http://schemas.google.com/g/2005#QQ";
    protected static final String IM_REL_GOOGLE_TALK = "http://schemas.google.com/g/2005#GOOGLE_TALK";
    protected static final String IM_REL_ICQ = "http://schemas.google.com/g/2005#ICQ";
    protected static final String IM_REl_JABBER = "http://schemas.google.com/g/2005#JABBER";

    @Key("@address")
    public String imAddress;

    @Key("@label")
    public String label;

//    @Key("@rel")
//    public String type;

    @Key("@protocol")
    public String protocol;

    @Key("@primary")
    public Boolean isPrimary;

    /**
     * Converts a Google phone number rel type to our phone number type
     */
    public IM_PROTOCOL convertProtocol() {
        if(protocol == null)
            return null;
        if(protocol.equals(IM_REL_AOL))
            return IM_PROTOCOL.AOL;
        if(protocol.equals(IM_REL_MSN))
            return IM_PROTOCOL.MSN;
        if(protocol.equals(IM_REL_YAHOO))
            return IM_PROTOCOL.YAHOO;
        if(protocol.equals(IM_REL_SKYPE))
            return IM_PROTOCOL.SKYPE;
        if(protocol.equals(IM_REL_QQ))
            return IM_PROTOCOL.QQ;
        if(protocol.equals(IM_REL_GOOGLE_TALK))
            return IM_PROTOCOL.GOOGLE_TALK;
        if(protocol.equals(IM_REL_ICQ))
            return IM_PROTOCOL.ICQ;
        if(protocol.equals(IM_REl_JABBER))
            return IM_PROTOCOL.JABBER;
        return null;
    }
}
