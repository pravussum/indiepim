package net.mortalsilence.indiepim.server.command.results;

import net.mortalsilence.indiepim.server.command.Result;

public class UserSettingsResult implements Result {

	private String googleAuthClientId;
	private String googleAuthClientSecret;

	public String getGoogleAuthClientId() {
		return googleAuthClientId;
	}

	public void setGoogleAuthClientId(String googleAuthClientId) {
		this.googleAuthClientId = googleAuthClientId;
	}

	public String getGoogleAuthClientSecret() {
		return googleAuthClientSecret;
	}

	public void setGoogleAuthClientSecret(String googleAuthClientSecret) {
		this.googleAuthClientSecret = googleAuthClientSecret;
	}

	public UserSettingsResult() {
	}
	
}
