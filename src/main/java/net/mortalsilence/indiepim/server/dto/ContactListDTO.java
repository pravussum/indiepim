package net.mortalsilence.indiepim.server.dto;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 26.11.12
 * Time: 20:23
 * To change this template use File | Settings | File Templates.
 */
public class ContactListDTO  implements Serializable {

    public Long contactId;
    public String displayName;
    public String primaryEmailAddress;
    public String primaryPhoneNo;
    public String primaryPostalAddress;
    public Set<TagDTO> tags = new TreeSet<TagDTO>();
}
