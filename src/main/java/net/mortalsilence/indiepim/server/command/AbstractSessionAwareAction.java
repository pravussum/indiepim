package net.mortalsilence.indiepim.server.command;

public class AbstractSessionAwareAction<R extends Result> implements SessionAwareAction<R> {

	private String sessionId;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
