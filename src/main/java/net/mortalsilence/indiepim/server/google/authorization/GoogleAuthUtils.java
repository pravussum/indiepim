package net.mortalsilence.indiepim.server.google.authorization;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import net.mortalsilence.indiepim.server.google.GoogleConstants;

import java.util.Collections;

public class GoogleAuthUtils {

	public static AuthorizationCodeFlow initializeFlow(final String clientId, final String clientSecret) {
		
		
		
		return new GoogleAuthorizationCodeFlow.Builder(new NetHttpTransport(), 
				new JacksonFactory(),
				clientId, 
				clientSecret,
				(Iterable<String>)Collections.singleton(GoogleConstants.GOOGLE_API_SCOPE_CONTACTS_V3)
				)
		.setCredentialStore(new UserConfigCredentialStore())
		.setAccessType("offline")
		.build();
	}	
	
}
