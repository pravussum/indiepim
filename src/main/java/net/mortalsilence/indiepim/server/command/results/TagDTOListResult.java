package net.mortalsilence.indiepim.server.command.results;

import net.mortalsilence.indiepim.server.command.Result;
import net.mortalsilence.indiepim.server.dto.TagDTO;

import java.util.Collection;

public class TagDTOListResult implements Result {

	private Collection<TagDTO> tags;

	public TagDTOListResult() {}
	
	public TagDTOListResult(Collection<TagDTO> tags) {
		this.tags = tags;
	}

	public Collection<TagDTO> getTags() {
		return tags;
	}
	
}
