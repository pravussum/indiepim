package net.mortalsilence.indiepim.server.command.handler.google;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import net.mortalsilence.indiepim.server.UserConfigKey;
import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.google.GetGoogleContactGroups;
import net.mortalsilence.indiepim.server.command.actions.google.GoogleContactGroupDTOListResult;
import net.mortalsilence.indiepim.server.dao.ConfigDAO;
import net.mortalsilence.indiepim.server.google.authorization.GoogleAuthUtils;
import net.mortalsilence.indiepim.server.google.contacts.GoogleContactUtils;
import net.mortalsilence.indiepim.server.google.contacts.GoogleContactsClient;
import net.mortalsilence.indiepim.server.google.contacts.model.GoogleContactGroup;
import net.mortalsilence.indiepim.server.command.handler.ActionUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.List;

@Named
public class GetGoogleContactGroupsHandler implements Command<GetGoogleContactGroups, GoogleContactGroupDTOListResult> {

    @Inject
    private ConfigDAO configDAO;

    @Transactional
	@Override
    public GoogleContactGroupDTOListResult execute(GetGoogleContactGroups action) {
	
		final Long userId = ActionUtils.getUserId();
		
		final String clientId = configDAO.getUserPropertyValue(userId, UserConfigKey.GOOGLE_AUTH_CLIENT_ID);
		final String clientSecret = configDAO.getUserPropertyValue(userId, UserConfigKey.GOOGLE_AUTH_CLIENT_SECRET);
		
		
		AuthorizationCodeFlow flow = GoogleAuthUtils.initializeFlow(clientId, clientSecret);
		Credential credential = null;
		try {
			credential = flow.loadCredential(userId.toString());
			final GoogleContactsClient client = new GoogleContactsClient(new NetHttpTransport().createRequestFactory(credential));
			List<GoogleContactGroup> groups = client.getAllContactGroups();
			return new GoogleContactGroupDTOListResult(GoogleContactUtils.mapGoogleContactGroup2DTO(groups));
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
		
		
	}

	@Override
	public void rollback(GetGoogleContactGroups arg0, GoogleContactGroupDTOListResult arg1) {
		// no use rolling back a getter
	}

}
