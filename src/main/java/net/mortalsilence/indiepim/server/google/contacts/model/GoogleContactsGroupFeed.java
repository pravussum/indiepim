package net.mortalsilence.indiepim.server.google.contacts.model;

import com.google.api.client.util.Key;

import java.util.List;

public class GoogleContactsGroupFeed {

	@Key("entry")
	public List<GoogleContactGroup> entries;
	
}
