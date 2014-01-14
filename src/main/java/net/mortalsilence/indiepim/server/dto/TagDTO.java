package net.mortalsilence.indiepim.server.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TagDTO implements Serializable, Comparable<TagDTO> {

	public String tag;
	public String color;
	
	@Override
	public int compareTo(TagDTO o) {
		return tag.compareTo(o.tag);	
	}
	
}
