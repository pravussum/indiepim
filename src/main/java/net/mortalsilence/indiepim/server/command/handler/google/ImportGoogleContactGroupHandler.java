package net.mortalsilence.indiepim.server.command.handler.google;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import net.mortalsilence.indiepim.server.UserConfigKey;
import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.google.ImportGoogleContactGroup;
import net.mortalsilence.indiepim.server.command.results.StringResult;
import net.mortalsilence.indiepim.server.dao.ConfigDAO;
import net.mortalsilence.indiepim.server.dao.ContactDAO;
import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.dao.UserDAO;
import net.mortalsilence.indiepim.server.domain.ContactPO;
import net.mortalsilence.indiepim.server.domain.ImagePO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import net.mortalsilence.indiepim.server.google.authorization.GoogleAuthUtils;
import net.mortalsilence.indiepim.server.google.contacts.GoogleContactMapper;
import net.mortalsilence.indiepim.server.google.contacts.GoogleContactsClient;
import net.mortalsilence.indiepim.server.google.contacts.model.GoogleContact;
import net.mortalsilence.indiepim.server.command.handler.ActionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Service
public class ImportGoogleContactGroupHandler implements Command<ImportGoogleContactGroup, StringResult> {

    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");
    @Inject
    private UserDAO userDAO;
    @Inject
    private GoogleContactMapper googleContactMapper;
    @Inject
    private ConfigDAO configDAO;
    @Inject
    private ContactDAO contactDAO;
    @Inject
    private GenericDAO genericDAO;

	@Transactional
    @Override
    public StringResult execute(ImportGoogleContactGroup action) {

        final UserPO user = userDAO.getUser(ActionUtils.getUserId());

		if(action.getGoogleContactGroupId() == null || "".equals(action.getGoogleContactGroupId()))
				throw new RuntimeException("Google contact group id is invalid.");
		
		final String clientId = configDAO.getUserPropertyValue(user.getId(), UserConfigKey.GOOGLE_AUTH_CLIENT_ID);
		final String clientSecret = configDAO.getUserPropertyValue(user.getId(), UserConfigKey.GOOGLE_AUTH_CLIENT_SECRET);
		
		AuthorizationCodeFlow flow = GoogleAuthUtils.initializeFlow(clientId, clientSecret);
		Credential credential = null;
		try {
			credential = flow.loadCredential(user.getId().toString());
			final GoogleContactsClient client = new GoogleContactsClient(new NetHttpTransport().createRequestFactory(credential));
			List<GoogleContact> googleContacts = client.getContacts4Group(action.getGoogleContactGroupId());

            final Iterator<GoogleContact> it = googleContacts.iterator();
            while(it.hasNext()) {
                final GoogleContact googleContact = it.next();
                ContactPO contact = contactDAO.getSingleContactByOrigin(googleContact.id, user.getId());
                if(contact == null) {
                    contact = new ContactPO();
                    contact.setUser(user);

                }
                /* map the contact attributes */
                googleContactMapper.mapGoogleContact2ContactPO(user, googleContact, contact);
                /* save the contact now, so that storing the email address does not fail (due to its relation to the unsaved contact) */
                contact = genericDAO.merge(contact);
                genericDAO.flush();
                /* map email addresses */
                googleContactMapper.mapGoogleContactEmailAddrToContactPoEmailAddr(user, googleContact, contact);
                /* map phone numbers */
                googleContactMapper.mapGoogleContactPhoneNosToContactPOPhoneNos(user, googleContact, contact);
                /* map postal addresses */
                googleContactMapper.mapGoogleContactPostalAddressesToContactPOAddresses(user, googleContact, contact);
                /* persist contact photo */
                final String photoUrl = googleContact.getPhotoUrl();
                if(photoUrl != null) {
                    if(logger.isDebugEnabled()) {
                        logger.debug("Photo for " + googleContact.title);
                        logger.debug(photoUrl);
                    }
                    final byte[] photo = client.getPhoto(photoUrl);
                    if(photo != null) {
                        ImagePO image = new ImagePO();
                        image.setUser(user);
                        image.setImage(photo);
                        contact.setPhoto(image);
                    }
                }
            }

			String photoUrl = googleContacts.get(0).getPhotoUrl();
            return new StringResult(client.getPhoto(photoUrl).toString());
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
		
	}

	@Override
	public void rollback(ImportGoogleContactGroup arg0, StringResult arg1) {
		// no use rolling back a getter
	}

}
