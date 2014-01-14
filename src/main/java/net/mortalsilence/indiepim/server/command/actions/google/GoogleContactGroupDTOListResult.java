package net.mortalsilence.indiepim.server.command.actions.google;

import net.mortalsilence.indiepim.server.command.Result;
import net.mortalsilence.indiepim.server.dto.GoogleContactGroupDTO;

import java.util.List;

public class GoogleContactGroupDTOListResult implements Result {

	private List<GoogleContactGroupDTO> googleContactGroups;

	public GoogleContactGroupDTOListResult() {}
	
	public GoogleContactGroupDTOListResult(List<GoogleContactGroupDTO> googleContactGroups) {
		this.googleContactGroups = googleContactGroups;
	}

	public List<GoogleContactGroupDTO> getGoogleContactGroups() {
		return googleContactGroups;
	}
	
}
