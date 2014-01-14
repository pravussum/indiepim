package net.mortalsilence.indiepim.server.utils;

import net.mortalsilence.indiepim.server.domain.TagHierarchyPO;
import net.mortalsilence.indiepim.server.domain.TagLineagePO;
import net.mortalsilence.indiepim.server.domain.TagPO;
import net.mortalsilence.indiepim.server.dto.TagDTO;
import net.mortalsilence.indiepim.server.tags.TagHierarchyTree;

import java.util.*;

public class TagUtils {

	public static TagHierarchyTree getTagHierarchyTree(final TagHierarchyPO tagHierarchy) {
		TagHierarchyTree tree = new TagHierarchyTree();
		
		List<TagLineagePO> tagLineages = new ArrayList<TagLineagePO>(tagHierarchy.getTagLineages());
		Collections.sort(tagLineages);
		
		for(TagLineagePO tagLineage : tagLineages) {
			tree.addPath(tagLineage.getLineage(), tagLineage.getId());
		}
		return tree;
	}
	
	public static Collection<TagDTO> mapTagPO2TagDTOList(final Collection<TagPO> tagPOs) {
		
		final Collection<TagDTO> result = new LinkedList<TagDTO>();
		final Iterator<TagPO> it = tagPOs.iterator();
		while(it.hasNext()) {
			final TagPO curTagPO = it.next();			
			final TagDTO tagDTO = new TagDTO();
			tagDTO.color = curTagPO.getColor();
			tagDTO.tag = curTagPO.getTag();
			result.add(tagDTO);
		}		
		
		return result;
	}

}
