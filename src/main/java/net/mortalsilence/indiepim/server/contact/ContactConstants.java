package net.mortalsilence.indiepim.server.contact;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 11.11.12
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */
public interface ContactConstants {

    public enum PHONE_NUMBER_TYPE {
        MOBILE,
        FAX,
        HOME,
        WORK,
        PAGER,
        OTHER
    }

    public enum ADDRESS_TYPE {
        HOME,
        WORK,
        OTHER
    }

    public enum IM_PROTOCOL {
        AOL,
        MSN,
        YAHOO,
        SKYPE,
        QQ,
        GOOGLE_TALK,
        ICQ,
        JABBER
    }

}
