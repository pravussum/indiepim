package net.mortalsilence.indiepim.server.utils;

import net.mortalsilence.indiepim.server.domain.*;
import net.mortalsilence.indiepim.server.dto.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 26.11.12
 * Time: 22:09
 * To change this template use File | Settings | File Templates.
 */
public class ContactUtils {

    public static List<ContactDTO> mapContactPO2ContactDTOList(final List<ContactPO> contactPOs) {
            final List<ContactDTO> result = new LinkedList<ContactDTO>();
            final Iterator<ContactPO> it = contactPOs.iterator();
            while(it.hasNext()) {
                final ContactPO curPO = it.next();
                final ContactDTO curDTO = mapContactPO2ContactDTO(curPO);

                result.add(curDTO);
            }
            return result;
        }

    public static List<ContactListDTO> mapContactPO2ContactListDTOs(final List<ContactPO> contactPOs) {
        final List<ContactListDTO> result = new LinkedList<ContactListDTO>();
        final Iterator<ContactPO> it = contactPOs.iterator();
        while(it.hasNext()) {
            final ContactPO curPO = it.next();
            final ContactListDTO curDTO = mapContactPO2ContactListDTO(curPO);

            result.add(curDTO);
        }
        return result;
    }

    public static ContactDTO mapContactPO2ContactDTO(ContactPO curPO) {
            final ContactDTO curDTO = new ContactDTO();
            curDTO.id = curPO.getId();
            curDTO.displayName = curPO.getDisplayName();
            curDTO.givenName = curPO.getGivenName();
            curDTO.familyName = curPO.getFamilyName();
            // TODO performance check: is the image data loaded or the id taken from contact column?
            curDTO.photoId = curPO.getPhoto().getId();

            final Set<TagPO> allTags = curPO.getAllTags();
            for(TagPO tag : allTags) {
                TagDTO tagDto = new TagDTO();
                tagDto.tag = tag.getTag();
                tagDto.color = tag.getColor();
                curDTO.tags.add(tagDto);
            }
            final Iterator<EmailAddressPO> emailIt = curPO.getEmailAddresses().iterator();
            while(emailIt.hasNext()) {
                final EmailAddressPO curEmailPO = emailIt.next();
                final EmailAddressDTO curEmailDTO = new EmailAddressDTO(curEmailPO.getId(),
                                                                        curEmailPO.getEmailAddress(),
                                                                        curEmailPO.getPrimary(),
                                                                        curEmailPO.getType());
                curDTO.emailAddresses.add(curEmailDTO);
            }

            final Iterator<AddressPO> addressIt = curPO.getAddresses().iterator();
            while(addressIt.hasNext()) {
                final AddressPO curAddressPO = addressIt.next();
                final PostalAddressDTO curAddressDTO = new PostalAddressDTO(curAddressPO.getId(),
                        curAddressPO.getAddress(),
                        curAddressPO.getPrimary(),
                        curAddressPO.getType()
                );
                curDTO.postalAddresses.add(curAddressDTO);
            }

            final Iterator<PhoneNoPO> phoneIt = curPO.getPhoneNos().iterator();
            while(phoneIt.hasNext()) {
                final PhoneNoPO curPhonePO = phoneIt.next();
                final PhoneNoDTO curPhoneDTO = new PhoneNoDTO(curPhonePO.getId(),
                        curPhonePO.getPhoneNo(),
                        curPhonePO.getPrimary(),
                        curPhonePO.getType());
                curDTO.phoneNos.add(curPhoneDTO);
            }
            return curDTO;
        }

    public static ContactListDTO mapContactPO2ContactListDTO(ContactPO curPO) {
        final ContactListDTO curDTO = new ContactListDTO();
        curDTO.contactId = curPO.getId();
        curDTO.displayName = curPO.getDisplayName();

        final Set<TagPO> allTags = curPO.getAllTags();
        for(TagPO tag : allTags) {
            TagDTO tagDto = new TagDTO();
            tagDto.tag = tag.getTag();
            tagDto.color = tag.getColor();
            curDTO.tags.add(tagDto);
        }
        final Iterator<EmailAddressPO> emailIt = curPO.getEmailAddresses().iterator();
        while(emailIt.hasNext()) {
            final EmailAddressPO curEmailPO = emailIt.next();
            if(Boolean.TRUE.equals(curEmailPO.getPrimary())) {
               curDTO.primaryEmailAddress = curEmailPO.getEmailAddress();
            }
        }
        /* if no primary address exists, take the first best one */
        if(curDTO.primaryEmailAddress == null && !curPO.getEmailAddresses().isEmpty())
            curDTO.primaryEmailAddress = curPO.getEmailAddresses().get(0).getEmailAddress();

        final Iterator<AddressPO> addressIt = curPO.getAddresses().iterator();
        while(addressIt.hasNext()) {
            final AddressPO curAddressPO = addressIt.next();
            if(Boolean.TRUE.equals(curAddressPO.getPrimary())) {
                curDTO.primaryPostalAddress = curAddressPO.getAddress();
            }
        }
        /* if no primary address exists, take the first best one */
        if(curDTO.primaryPostalAddress == null && !curPO.getAddresses().isEmpty())
            curDTO.primaryPostalAddress = curPO.getAddresses().get(0).getAddress();

        final Iterator<PhoneNoPO> phoneIt = curPO.getPhoneNos().iterator();
        while(phoneIt.hasNext()) {
            final PhoneNoPO curPhonePO = phoneIt.next();
            if(Boolean.TRUE.equals(curPhonePO.getPrimary())) {
                curDTO.primaryPhoneNo = curPhonePO.getPhoneNo();
            }
        }
        /* if no primary phone exists, take the first best one */
        if(curDTO.primaryPhoneNo == null && !curPO.getPhoneNos().isEmpty())
                    curDTO.primaryPhoneNo = curPO.getPhoneNos().get(0).getPhoneNo();
        return curDTO;
    }

}
