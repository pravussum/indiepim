package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.StringResult;

public class StartGoogleAuth extends AbstractSessionAwareAction<StringResult> {

	private String clientId;
	private String clientSecret;
	
	public StartGoogleAuth() {}
	
	public StartGoogleAuth(String clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	
}
