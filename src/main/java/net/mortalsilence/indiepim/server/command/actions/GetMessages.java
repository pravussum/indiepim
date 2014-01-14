package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.MessageListResult;

public class GetMessages extends AbstractSessionAwareAction<MessageListResult> {

	public GetMessages() {
		
	}
	private Integer offset;
	private Integer pageSize;
	private Long accountId;
	private String tagName;
	private Long tagLineageId;
	private String searchTerm;
    private Boolean read;
	
	public Integer getPageSize() {
		return pageSize;
	}
	
	public GetMessages(Integer offset, Integer pageSize, Long accountId, String tagName, Long tagLineageId, String searchTerm, Boolean read) {
		this.offset = offset;
		this.pageSize = pageSize;
		this.accountId = accountId;
		this.tagName = tagName;
		this.tagLineageId = tagLineageId;
		this.searchTerm = searchTerm;
        this.read = read;
	}
	
	public Integer getOffset() {
		return offset;
	}

	public Long getAccountId() {
		return accountId;
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

    public Boolean getRead() {
        return read;
    }
}
