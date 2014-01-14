package net.mortalsilence.indiepim.server.google.authorization;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import net.mortalsilence.indiepim.server.UserConfigKey;
import net.mortalsilence.indiepim.server.dao.ConfigDAO;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;

@Named
public class UserConfigCredentialStore implements CredentialStore {

    @Inject
    private ConfigDAO configDAO;

	@Override
	public boolean load(String userId, Credential credential) throws IOException {
		credential.setAccessToken(configDAO.getUserPropertyValue(new Long(userId), UserConfigKey.GOOGLE_AUTH_ACCESS_TOKEN));
		if(credential.getAccessToken() == null)
			return false;
		credential.setRefreshToken(configDAO.getUserPropertyValue(new Long(userId), UserConfigKey.GOOGLE_AUTH_REFRESH_TOKEN));
		credential.setExpirationTimeMilliseconds(new Long(configDAO.getUserPropertyValue(new Long(userId), UserConfigKey.GOOGLE_AUTH_EXPIRATION_TIME)));
		if(credential.getExpirationTimeMilliseconds() == null)
			return false;
		return true;
	}

	@Override
	public void store(String userId, Credential credential) throws IOException {
		configDAO.setUserPropertyValue(new Long(userId), UserConfigKey.GOOGLE_AUTH_ACCESS_TOKEN, credential.getAccessToken());
		configDAO.setUserPropertyValue(new Long(userId), UserConfigKey.GOOGLE_AUTH_REFRESH_TOKEN, credential.getRefreshToken());
		configDAO.setUserPropertyValue(new Long(userId), UserConfigKey.GOOGLE_AUTH_EXPIRATION_TIME, credential.getExpirationTimeMilliseconds().toString());
	}

	@Override
	public void delete(String userId, Credential credential) throws IOException {
		configDAO.deleteUserProperty(new Long(userId), UserConfigKey.GOOGLE_AUTH_ACCESS_TOKEN);
		configDAO.deleteUserProperty(new Long(userId), UserConfigKey.GOOGLE_AUTH_REFRESH_TOKEN);
		configDAO.deleteUserProperty(new Long(userId), UserConfigKey.GOOGLE_AUTH_EXPIRATION_TIME);

	}

}
