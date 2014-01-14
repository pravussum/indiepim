package net.mortalsilence.indiepim.server.google.contacts.model;

import com.google.api.client.util.Key;
import net.mortalsilence.indiepim.server.contact.ContactConstants;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 11.11.12
 * Time: 00:14
 * To change this template use File | Settings | File Templates.
 */
public class GoogleContactPhoneNumber implements ContactConstants {

    @Key("@label")
    public String label;

    @Key("@rel")
    public String type;

    @Key("@primary")
    public Boolean isPrimary;

    @Key("text()")
    public String number;


    private static String PHONE_NUMBER_REL_MOBILE = "http://schemas.google.com/g/2005#mobile";
    private static String PHONE_NUMBER_REL_OTHER = "http://schemas.google.com/g/2005#other";
    private static String PHONE_NUMBER_REL_FAX = "http://schemas.google.com/g/2005#fax";
    private static String PHONE_NUMBER_REL_HOME = "http://schemas.google.com/g/2005#home";
    private static String PHONE_NUMBER_REL_HOME_FAX = "http://schemas.google.com/g/2005#home_fax";
    private static String PHONE_NUMBER_REL_WORK = "http://schemas.google.com/g/2005#work";
    private static String PHONE_NUMBER_REL_WORK_MOBILE = "http://schemas.google.com/g/2005#work_mobile";
    private static String PHONE_NUMBER_REL_WORK_PAGER = "http://schemas.google.com/g/2005#work_pager";
    private static String PHONE_NUMBER_REL_WORK_FAX = "http://schemas.google.com/g/2005#work_fax";

    /**
     * Converts a Google phone number rel type to our phone number type
     */
    public PHONE_NUMBER_TYPE convertPhoneNumberType() {
        if(type == null)
            return PHONE_NUMBER_TYPE.OTHER;
        if(type.equals(PHONE_NUMBER_REL_HOME))
            return PHONE_NUMBER_TYPE.HOME;
        if(type.equals(PHONE_NUMBER_REL_WORK) ||
           type.equals(PHONE_NUMBER_REL_WORK_MOBILE) ||
           type.equals(PHONE_NUMBER_REL_WORK_PAGER))
            return PHONE_NUMBER_TYPE.WORK;
        if(type.equals(PHONE_NUMBER_REL_FAX) ||
           type.equals(PHONE_NUMBER_REL_HOME_FAX) ||
           type.equals(PHONE_NUMBER_REL_WORK_FAX))
             return PHONE_NUMBER_TYPE.FAX;
        if(type.equals(PHONE_NUMBER_REL_MOBILE))
            return PHONE_NUMBER_TYPE.MOBILE;
        if(type.equals(PHONE_NUMBER_REL_OTHER))
            return PHONE_NUMBER_TYPE.OTHER;

        /* Defaults to OTHER */
        return PHONE_NUMBER_TYPE.OTHER;
    }

}
