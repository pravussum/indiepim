package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.TagDTOListResult;

public class GetTags extends AbstractSessionAwareAction<TagDTOListResult> {

	private Long tagLineageId;
    private String query;
	
	public GetTags() {
	}

    public GetTags(Long tagLineageId, String query) {
        this.tagLineageId = tagLineageId;
        this.query = query;
    }

    public Long getTagLineageId() {
		return tagLineageId;
	}

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setTagLineageId(Long tagLineageId) {
		this.tagLineageId = tagLineageId;
	}
}
