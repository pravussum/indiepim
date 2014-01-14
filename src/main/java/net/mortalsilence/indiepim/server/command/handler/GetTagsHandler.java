package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.GetTags;
import net.mortalsilence.indiepim.server.command.results.TagDTOListResult;
import net.mortalsilence.indiepim.server.dao.TagDAO;
import net.mortalsilence.indiepim.server.domain.TagPO;
import net.mortalsilence.indiepim.server.utils.TagUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collection;

@Service
public class GetTagsHandler implements Command<GetTags, TagDTOListResult> {

    @Inject
    private TagDAO tagDAO;

    @Transactional(readOnly = true)
	@Override
    public TagDTOListResult execute(GetTags action) {
        final Collection<TagPO> tags;
        if(action.getTagLineageId() != null) {
			 tags = tagDAO.getAllTagsForTagLineage(ActionUtils.getUserId(), action.getTagLineageId());
        } else if(action.getQuery() != null) {
            tags = tagDAO.searchForTags(ActionUtils.getUserId(), action.getQuery());
        } else {
            tags = tagDAO.getAllTags(ActionUtils.getUserId());
        }
        return new TagDTOListResult(TagUtils.mapTagPO2TagDTOList(tags));
	}

	@Override
	public void rollback(GetTags arg0, TagDTOListResult arg1) {
		// no use rolling back a getter
	}

}
