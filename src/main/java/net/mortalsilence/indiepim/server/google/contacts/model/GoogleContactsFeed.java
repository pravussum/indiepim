package net.mortalsilence.indiepim.server.google.contacts.model;

import com.google.api.client.util.Key;

import java.util.List;

public class GoogleContactsFeed {

	@Key("entry")
	public List<GoogleContact> entries;
	
}
