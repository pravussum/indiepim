package net.mortalsilence.indiepim.server.google.contacts;

import net.mortalsilence.indiepim.server.dto.GoogleContactGroupDTO;
import net.mortalsilence.indiepim.server.google.contacts.model.GoogleContactGroup;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GoogleContactUtils {

	public static List<GoogleContactGroupDTO> mapGoogleContactGroup2DTO(Collection<GoogleContactGroup> groups) {
		if(groups == null)
			return null;
		final Iterator<GoogleContactGroup> it = groups.iterator();
		final List<GoogleContactGroupDTO> result = new LinkedList<GoogleContactGroupDTO>(); 
		while(it.hasNext()) {
			final GoogleContactGroup group = it.next();
			GoogleContactGroupDTO groupDTO = new GoogleContactGroupDTO(group.id, group.title);
			result.add(groupDTO);
		}
		return result;
	}
	
	
}
