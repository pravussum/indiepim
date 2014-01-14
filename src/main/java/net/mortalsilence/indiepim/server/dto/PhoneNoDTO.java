package net.mortalsilence.indiepim.server.dto;

import net.mortalsilence.indiepim.server.contact.ContactConstants;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 26.11.12
 * Time: 21:46
 * To change this template use File | Settings | File Templates.
 */
public class PhoneNoDTO implements Serializable {

    public Long id;
    public ContactConstants.PHONE_NUMBER_TYPE type;
    public String phoneNo;
    public Boolean isPrimary;

    public PhoneNoDTO(Long id, String phoneNo, Boolean primary, ContactConstants.PHONE_NUMBER_TYPE type) {
        this.id = id;
        this.type = type;
        this.phoneNo = phoneNo;
        isPrimary = primary;
    }

    public PhoneNoDTO() {
    }
}
