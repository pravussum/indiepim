package net.mortalsilence.indiepim.server.dao;

import net.mortalsilence.indiepim.server.SharedConstants;
import net.mortalsilence.indiepim.server.domain.TagHierarchyPO;
import net.mortalsilence.indiepim.server.domain.TagLineagePO;
import net.mortalsilence.indiepim.server.domain.TagPO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;

@Named
public class TagDAO {

	@PersistenceContext
	private EntityManager em;
    @Inject
    private GenericDAO genericDAO;
    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");

	/**
	 * Returns the tag object with tag name <i>tag</i> owned by the user with id <i>userId</i> or
	 * null if no such tag exists.
	 * @param tag
	 * @param userId
	 * @return
	 */
	public TagPO getTagByName(final String tag, final Long userId) {
		try {
			return em.createQuery("from TagPO where tag = ?1 and user.id = ?2", TagPO.class)
				.setParameter(1, tag)
				.setParameter(2, userId)
				.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}	
	
	public TagLineagePO getTagLineage(final Long userId, final String tagLineage, final Long tagHierarchyId) {
		try {
			return em.createQuery("from TagLineagePO where user.id = ?1 and lineage = ?2 and tagHierarchy.id = ?3", TagLineagePO.class)
				.setParameter(1, userId)
				.setParameter(2, tagLineage)
				.setParameter(3, tagHierarchyId)
				.getSingleResult();
		} catch(NoResultException nre) {
			return null;
		} 
	}
	
	public Collection<TagPO> getAllTagsForTagLineage(final Long userId, final Long tagLineageId) {
		TagLineagePO tl = em.createQuery("from TagLineagePO where user.id = ?1 and id = ?2", TagLineagePO.class)
			.setParameter(1, userId)
			.setParameter(2, tagLineageId)
			.getSingleResult();
		return tl.getTags();			
	}

    public Collection<TagPO> getAllTags(final Long userId) {
        return em.createQuery("from TagPO where user.id = ?1", TagPO.class)
                .setParameter(1, userId)
                .getResultList();
    }

    public Collection<TagPO> searchForTags(final Long userId, final String query) {
        return em.createQuery("from TagPO where user.id = ?1 and tag like ?2", TagPO.class)
                .setParameter(1, userId)
                .setParameter(2, "%" + query + "%")
                .getResultList();
    }

	public TagLineagePO getOrCreateTagLineage(final UserPO user, final String tagLineageStr, final TagHierarchyPO tagHierarchy) {		
		if(tagHierarchy == null)
			throw new IllegalArgumentException("tagHierarchy must not be null!");
		TagLineagePO tagLineage = getTagLineage(user.getId(), tagLineageStr, tagHierarchy.getId());
		if(tagLineage != null)
			return tagLineage;
		
		tagLineage = new TagLineagePO();
		tagLineage.setUser(user);
		tagLineage.setLineage(tagLineageStr);
		tagLineage.setTagHierarchy(tagHierarchy);
		String[] tags = tagLineageStr.split(SharedConstants.TAG_LINEAGE_SEPARATOR.toString());
		for(int i=0; i<tags.length; i++) {
			final TagPO tag = getOrCreateTag(user, tags[i]);
			tagLineage.getTags().add(tag);
		}
		genericDAO.persist(tagLineage);
		return tagLineage;
	}

	public TagPO getOrCreateTag(final UserPO user, final String tagStr) {
		TagPO tag = getTagByName(tagStr, user.getId());
		if(tag != null)
			return tag;
		tag = new TagPO();
		tag.setTag(tagStr);
		tag.setUser(user);
		genericDAO.persist(tag);
		return tag;
	}
	
	public List<Long> getAllMsgUidsForTagLineage(final Long userId, final Long tagLineageId) {
		return em.createQuery("select map.msgUid from MessageTagLineageMappingPO map join map.tagLineage t where t.user.id = ?1 and t.id =?2", Long.class)
			.setParameter(1, userId)
			.setParameter(2, tagLineageId)
			.getResultList();				
	}
	
}
