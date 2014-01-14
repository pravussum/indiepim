package net.mortalsilence.indiepim.server.command.results;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.mortalsilence.indiepim.server.command.Result;

public class IdResult implements Result {

	@JsonProperty("id") protected Long id;

	public Long getId() {
		return id;
	}

	public IdResult(Long id) {
		super();
		this.id = id;
	}

	public IdResult() {
	}
	
}
