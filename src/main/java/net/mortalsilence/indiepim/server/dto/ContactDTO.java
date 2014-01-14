package net.mortalsilence.indiepim.server.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 26.11.12
 * Time: 20:22
 * To change this template use File | Settings | File Templates.
 */
public class ContactDTO implements Serializable {

    public Long id;
    public String displayName;
    public String familyName;
    public String givenName;
    public List<EmailAddressDTO> emailAddresses;
    public List<PhoneNoDTO> phoneNos;
    public List<PostalAddressDTO> postalAddresses;
    // TODO dates
    // TODO urls
    public Set<TagDTO> tags = new TreeSet<TagDTO>();
    public Long photoId;
}
