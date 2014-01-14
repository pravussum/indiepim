package net.mortalsilence.indiepim.server.command.actions.google;

import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.StringResult;


public class ImportGoogleContactGroup extends AbstractSessionAwareAction<StringResult>{

	private String googleContactGroupId;
	
	public ImportGoogleContactGroup() {
	}	

	public ImportGoogleContactGroup(String googleContactGroupId) {
		this.googleContactGroupId = googleContactGroupId;
	}

	public String getGoogleContactGroupId() {
		return googleContactGroupId;
	}

}
