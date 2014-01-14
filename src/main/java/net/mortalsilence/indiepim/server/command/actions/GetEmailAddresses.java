package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.EmailAddressListResult;

public class GetEmailAddresses extends AbstractSessionAwareAction<EmailAddressListResult> {

    private String query;

	public GetEmailAddresses() {
	}

    public GetEmailAddresses(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
