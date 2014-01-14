package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.ContactListResult;

public class GetContacts extends AbstractSessionAwareAction<ContactListResult> {

	public GetContacts() {
	}
	private Integer offset;
	private Integer pageSize;
	private String tagName;
	private Long tagLineageId;
	private String searchTerm;

	public Integer getPageSize() {
		return pageSize;
	}

	public GetContacts(Integer offset, Integer pageSize, String tagName, Long tagLineageId, String searchTerm) {
		this.offset = offset;
		this.pageSize = pageSize;
		this.tagName = tagName;
		this.tagLineageId = tagLineageId;
		this.searchTerm = searchTerm;
	}
	
	public Integer getOffset() {
		return offset;
	}

	public String getTagName() {
		return tagName;
	}

	public Long getTagLineageId() {
		return tagLineageId;
	}

	public String getSearchTerm() {
		return searchTerm;
	}
	
}
