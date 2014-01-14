package net.mortalsilence.indiepim.server.command.handler.google;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import net.mortalsilence.indiepim.server.SystemConfigKey;
import net.mortalsilence.indiepim.server.UserConfigKey;
import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.StartGoogleAuth;
import net.mortalsilence.indiepim.server.command.results.StringResult;
import net.mortalsilence.indiepim.server.dao.ConfigDAO;
import net.mortalsilence.indiepim.server.google.authorization.GoogleAuthUtils;
import net.mortalsilence.indiepim.server.command.handler.ActionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class StartGoogleAuthHandler implements Command<StartGoogleAuth, StringResult> {

    @Inject
    private ConfigDAO configDAO;

	/** Lock on the flow and credential. */
	private final Lock lock = new ReentrantLock();

	/**
	 * Authorization code flow to be used across all HTTP servlet requests or {@code null} before
	 * initialized in {@link #initializeFlow()}.
	 */
	private AuthorizationCodeFlow flow;	

    @Transactional
	@Override
    public StringResult execute(StartGoogleAuth action) {
		final Long userId = ActionUtils.getUserId();

		final String clientId = action.getClientId();
		final String clientSecret = action.getClientSecret();

		configDAO.setUserPropertyValue(userId, UserConfigKey.GOOGLE_AUTH_CLIENT_ID, clientId);
		configDAO.setUserPropertyValue(userId, UserConfigKey.GOOGLE_AUTH_CLIENT_SECRET, clientSecret);
		
		
		lock.lock();
		try {
			// load credential from persistence store
			// always recreate the flow
			flow = GoogleAuthUtils.initializeFlow(clientId, clientSecret);
			Credential credential = null;
			try {
				credential = flow.loadCredential(userId.toString());
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
			// if credential found with an access token, invoke the user code
			if (credential != null && credential.getAccessToken() != null) {
				// we already have an access token
				return new StringResult("OK");
			}
			// redirect to the authorization flow
			AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl();
			
			final String redirectUri = getRedirectUri();
			authorizationUrl.setRedirectUri(redirectUri);
			authorizationUrl.setState(userId.toString());
			return new StringResult(authorizationUrl.build());
		} finally {
			lock.unlock();
		}
	}

	private String getRedirectUri() {
		String baseUrl = configDAO.getSystemPropertyValue(SystemConfigKey.SERVER_BASE_URL);
		if(baseUrl == null)
			throw new RuntimeException("No valid server base url configured.");
		final String redirectUri = baseUrl + "/oauth2callback";
		return redirectUri;
	}

	@Override
	public void rollback(StartGoogleAuth arg0, StringResult arg1) {
		// no use rolling back a getter
	}

}
