package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.UserDTOListResult;

public class GetUsers extends AbstractSessionAwareAction<UserDTOListResult> {

    private Boolean onlineOnly = Boolean.FALSE;

	public GetUsers() {
	}

    public GetUsers(Boolean onlineOnly) {
        this.onlineOnly = onlineOnly;
    }

    public Boolean getOnlineOnly() {
        return onlineOnly;
    }

    public void setOnlineOnly(Boolean onlineOnly) {
        this.onlineOnly = onlineOnly;
    }
}
