package net.mortalsilence.indiepim.server.dto;

import net.mortalsilence.indiepim.server.contact.ContactConstants;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 26.11.12
 * Time: 21:43
 * To change this template use File | Settings | File Templates.
 */
public class PostalAddressDTO implements Serializable, ContactConstants {

   	public Long id;
    public ContactConstants.ADDRESS_TYPE type;
    public String address;
    public Boolean isPrimary;

    public PostalAddressDTO(Long id, String address, Boolean primary, ADDRESS_TYPE type) {
        this.id = id;
        this.type = type;
        this.address = address;
        isPrimary = primary;
    }

    public PostalAddressDTO() {
    }
}
