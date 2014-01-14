package net.mortalsilence.indiepim.server.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GoogleContactGroupDTO implements Serializable {

	public String id;
	public String title;

	public GoogleContactGroupDTO() {
		super();
	}

	public GoogleContactGroupDTO(String id, String title) {
		this.id = id;
		this.title = title;
	}
}
