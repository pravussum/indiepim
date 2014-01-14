package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.CometMessagesResult;

public class GetCometMessages extends AbstractSessionAwareAction<CometMessagesResult> {

    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public GetCometMessages() {
	}

    public GetCometMessages(String sessionId) {
        this.sessionId = sessionId;
    }
}
