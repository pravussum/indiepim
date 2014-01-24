package net.mortalsilence.indiepim.server.dto;

import net.mortalsilence.indiepim.server.utils.StringUtils;

import java.util.Iterator;

public class DTOUtils {

	public static MessageListDTO map2MessageListDTO(final MessageDTO messageDTO) {
		final MessageListDTO messageListDTO = new MessageListDTO();
		messageListDTO.dateReceived = messageDTO.dateReceived;
		messageListDTO.msgId = messageDTO.id;
		messageListDTO.read = messageDTO.read;
        messageListDTO.deleted = messageDTO.deleted;
		messageListDTO.sender = messageDTO.sender;
		messageListDTO.subject = messageDTO.subject;
		messageListDTO.hasAttachment = messageDTO.hasAttachment;
        messageListDTO.tags = messageDTO.tags;
        // TODO handle HTML only mails
        if(messageDTO.contentText != null)
            messageListDTO.contentPreview = StringUtils.extractContentPreview(messageDTO.contentText);
		return messageListDTO;
	}

    public static ContactListDTO map2ContactListDTO(final ContactDTO contactDTO) {
        final ContactListDTO contactListDTO = new ContactListDTO();
        contactListDTO.contactId = contactDTO.id;
        contactListDTO.displayName = contactDTO.displayName;
        final Iterator<EmailAddressDTO> emailIt = contactDTO.emailAddresses.iterator();
        while(emailIt.hasNext()) {
            final EmailAddressDTO e = emailIt.next();
            if(e.isPrimary)
                contactListDTO.primaryEmailAddress = e.emailAddress;
        }
        final Iterator<PhoneNoDTO> phoneIt = contactDTO.phoneNos.iterator();
        while(phoneIt.hasNext()) {
            final PhoneNoDTO p = phoneIt.next();
            if(p.isPrimary)
                contactListDTO.primaryPhoneNo = p.phoneNo;
        }
        final Iterator<PostalAddressDTO> addressIt = contactDTO.postalAddresses.iterator();
        while(addressIt.hasNext()) {
            final PostalAddressDTO a = addressIt.next();
            if(a.isPrimary)
                contactListDTO.primaryPostalAddress = a.address;
        }
        contactListDTO.tags = contactDTO.tags;
        return contactListDTO;
    }
	
}
