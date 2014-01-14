package net.mortalsilence.indiepim.server.google.contacts;

import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.domain.*;
import net.mortalsilence.indiepim.server.google.contacts.model.GoogleContact;
import net.mortalsilence.indiepim.server.google.contacts.model.GoogleContactEmailAddress;
import net.mortalsilence.indiepim.server.google.contacts.model.GoogleContactPhoneNumber;
import net.mortalsilence.indiepim.server.google.contacts.model.GoogleContactStructuredAddress;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 12.11.12
 * Time: 21:51
 * To change this template use File | Settings | File Templates.
 */

@Service
public class GoogleContactMapper {

    @Inject
    private MessageDAO messageDAO;
    @Inject
    private GenericDAO genericDAO;

    public void mapGoogleContact2ContactPO(UserPO user, final GoogleContact googleContact, final ContactPO contact) {

        /* do not override an existing display name - really? */
        if(contact.getDisplayName() == null)
            contact.setDisplayName(googleContact.title);
        try {
            contact.setGivenName(googleContact.name.givenName.givenName);
        } catch (NullPointerException npe) {
            contact.setGivenName(null); }
        try {
            contact.setFamilyName(googleContact.name.familyName.familyName);
        } catch(NullPointerException npe) {
            contact.setFamilyName(null);
        }
        contact.setOrigin(googleContact.id);

        // TODO detect phone number duplicates
        // TODO search for email addresses and link to contact (who is responsible for relation?)
    }

    public void mapGoogleContactEmailAddrToContactPoEmailAddr(UserPO user, GoogleContact googleContact, ContactPO contact) {
        if(googleContact.emailAddresses != null) {
            final Iterator<GoogleContactEmailAddress> googleEmailIt = googleContact.emailAddresses.iterator();
            while(googleEmailIt.hasNext()) {
                GoogleContactEmailAddress googleEmail = googleEmailIt.next();
                EmailAddressPO emailAddr = messageDAO.getEmailAddress(user.getId(), googleEmail.emailAddress);
                if(emailAddr == null) {
                    emailAddr = new EmailAddressPO();
                    emailAddr.setUser(user);
                }
                emailAddr.setContact(contact);
                emailAddr.setEmailAddress(googleEmail.emailAddress);
                emailAddr.setType(googleEmail.convertEmailAddressType());
                emailAddr.setPrimary(googleEmail.isPrimary);
                emailAddr = genericDAO.merge(emailAddr);
            }
        }
    }

    public void mapGoogleContactPhoneNosToContactPOPhoneNos(UserPO user, GoogleContact googleContact, ContactPO contact) {
        if(googleContact.phoneNumbers != null) {

            final Iterator<PhoneNoPO> phoneNoPOIt = contact.getPhoneNos().iterator();
            final Map<String, PhoneNoPO> phoneNos = new HashMap<String, PhoneNoPO>();
            /* cache numbers and reset primary flags */
            while(phoneNoPOIt.hasNext()) {
                final PhoneNoPO phoneNoPO = phoneNoPOIt.next();
                phoneNoPO.setPrimary(false);
                phoneNos.put(phoneNoPO.getPhoneNo(), phoneNoPO);
            }


            final Iterator<GoogleContactPhoneNumber> googlePhoneNoIt = googleContact.phoneNumbers.iterator();
            while(googlePhoneNoIt.hasNext()) {
                GoogleContactPhoneNumber googlePhoneNumber = googlePhoneNoIt.next();

                /* already exists */
                if(phoneNos.keySet().contains(googlePhoneNumber.number)) {
                    phoneNos.get(googlePhoneNumber.number).setPrimary(googlePhoneNumber.isPrimary);
                    continue;
                }

                PhoneNoPO phoneNo = new PhoneNoPO();
                phoneNo.setUser(user);

                phoneNo.setContact(contact);
                phoneNo.setPhoneNo(googlePhoneNumber.number);
                phoneNo.setType(googlePhoneNumber.convertPhoneNumberType());
                phoneNo.setPrimary(googlePhoneNumber.isPrimary);
                phoneNo = genericDAO.merge(phoneNo);
            }
        }
    }

    public void mapGoogleContactPostalAddressesToContactPOAddresses(UserPO user, GoogleContact googleContact, ContactPO contact) {
        if(googleContact.structuredAddresses != null) {

            final Iterator<AddressPO> addressPOIt = contact.getAddresses().iterator();
            final Map<String, AddressPO> addressPOs = new HashMap<String, AddressPO>();
            /* cache numbers and reset primary flags */
            while(addressPOIt.hasNext()) {
                final AddressPO addressPO = addressPOIt.next();
                addressPO.setPrimary(false);
                addressPOs.put(addressPO.getAddress(), addressPO);
            }


            final Iterator<GoogleContactStructuredAddress> googleAddressIt = googleContact.structuredAddresses.iterator();
            while(googleAddressIt.hasNext()) {
                GoogleContactStructuredAddress googleAddress = googleAddressIt.next();

                /* already exists */
                if(addressPOs.keySet().contains(googleAddress.formattedAddress)) {
                    addressPOs.get(googleAddress.formattedAddress).setPrimary(googleAddress.isPrimary);
                    continue;
                }

                AddressPO addressPO = new AddressPO();
                addressPO.setUser(user);

                addressPO.setContact(contact);
                addressPO.setAddress(googleAddress.formattedAddress);
                addressPO.setType(googleAddress.convertAddressType());
                addressPO.setPrimary(googleAddress.isPrimary);
                addressPO = genericDAO.merge(addressPO);
            }
        }
    }
}
