package net.mortalsilence.indiepim.server.command;

public interface SessionAwareAction<R extends Result> extends Action<R> {
	
	public void setSessionId(String sessionId);
	public String getSessionId();	
}
