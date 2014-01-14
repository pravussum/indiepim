package net.mortalsilence.indiepim.server.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.mortalsilence.indiepim.server.message.MessageConstants;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 13.11.12
 * Time: 23:17
 * To change this template use File | Settings | File Templates.
 */
public class EmailAddressDTO implements Serializable {

    public EmailAddressDTO(Long id, final String emailAddress, final String contactName) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.contactName = contactName;
    }

    public EmailAddressDTO(final Long id, final String emailAddress, final Boolean isPrimary,
                           final MessageConstants.EMAIL_ADDRESS_TYPE type) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.isPrimary = isPrimary;
        this.type = type;
    }

    public EmailAddressDTO() {
    }

    @JsonProperty("id")public Long id;
    @JsonProperty("emailAddress") public String emailAddress;
    @JsonProperty("contactName") public String contactName;
    @JsonProperty("isPrimary") public Boolean isPrimary;
    @JsonProperty("type") public MessageConstants.EMAIL_ADDRESS_TYPE type;
    @JsonProperty("displayAddress") public String getDisplayAddresss () {
        if(contactName != null && !"".equals(contactName)) {
            return contactName + "<" + emailAddress + ">";
        } else {
            return emailAddress;
        }
    }
    @JsonProperty("imageUrl") private String imageUrl;

}
