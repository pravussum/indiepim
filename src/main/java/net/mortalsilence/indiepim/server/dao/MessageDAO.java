package net.mortalsilence.indiepim.server.dao;

import net.mortalsilence.indiepim.server.domain.*;
import org.apache.log4j.Logger;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.*;

@Named
public class MessageDAO {

	@PersistenceContext
    private EntityManager em;
    @Inject
    private GenericDAO genericDAO;
    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");


	public Long getAllMessagesTotalCount(final Long userId) {
		return em.createQuery("select count(*) from MessagePO where user.id = ?1", Long.class)
			.setParameter(1, userId)
			.getSingleResult();
	}

	public List<MessagePO> getAllMessages(final Long userId, final Integer firstResult, final Integer maxResults) {

		return em.createQuery("from MessagePO where user.id = ?1 order by dateReceived desc", MessagePO.class)
			.setParameter(1, userId)
			.setFirstResult(firstResult)
			.setMaxResults(maxResults)
			.getResultList();
	}

    public Long getMessagesByReadFlagTotalCount(final Long userId, final Boolean read) {
   		return em.createQuery("select count(*) from MessagePO where user.id = ?1 and read = ?2", Long.class)
   			.setParameter(1, userId)
            .setParameter(2, read)
   			.getSingleResult();
   	}

   	public List<MessagePO> getMessagesByReadFlag(final Long userId, final Boolean read, final Integer firstResult, final Integer maxResults) {

   		return em.createQuery("from MessagePO where user.id = ?1 and read = ?2 order by dateReceived desc", MessagePO.class)
   			.setParameter(1, userId)
            .setParameter(2, read)
   			.setFirstResult(firstResult)
   			.setMaxResults(maxResults)
   			.getResultList();
   	}

    public int updateDeletedFlags(final Long userId, final Collection<Long> msgIds, final boolean deleted) {
        return em.createQuery("update MessagePO set deleted = :deleted where :msgIds in :msgIds and user.id = :userId")
                .setParameter("deleted", deleted)
                .setParameter("msgIds", msgIds)
                .setParameter("userId", userId)
                .executeUpdate();
    }

	public Long getMessagesForAccountTotalCount(final Long userId, final Long accountId) {
		return  em.createQuery("select count(*) from MessagePO where user.id = ?1 and messageAccount.id = ?2", Long.class)
			.setParameter(1, userId)
			.setParameter(2, accountId)
			.getSingleResult();
	}

	public List<MessagePO> getMessagesForAccount(final Long userId, final Long accountId, final Integer firstResult, final Integer maxResults) {

		return  em.createQuery("from MessagePO where user.id = ?1 and messageAccount.id = ?2 order by dateReceived desc", MessagePO.class)
			.setParameter(1, userId)
			.setParameter(2, accountId)
			.setFirstResult(firstResult)
			.setMaxResults(maxResults)
			.getResultList();
	}	


	public Long getMessagesForTagTotalCount(final Long userId, final String tag) {
		return  ((BigInteger)em.createNativeQuery("select count(*) from msg_tag_view v, tag t where v.user_id = ?1 and t.id = v.tag_id and t.tag = ?2")
			.setParameter(1, userId)
			.setParameter(2, tag)
			.getSingleResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	public List<MessagePO> getMessagesForTag(final Long userId, final String tag, final Integer firstResult, final Integer maxResults) {
		return em.createNativeQuery("select m.* from msg_tag_view v, tag t, message m where v.user_id = ?1 and t.id = v.tag_id and t.tag = ?2 and m.id = v.message_id order by m.date_received desc", MessagePO.class)
			.setParameter(1, userId)
			.setParameter(2, tag)
			.setFirstResult(firstResult)
			.setMaxResults(maxResults)
			.getResultList();
	}

    public List<MessagePO> searchForMessages(final Long userId, final String searchExpression, final Integer firstResult, final Integer maxResults) {

        // TODO sort!
        final FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
        final QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(MessagePO.class).get();
        org.apache.lucene.search.Query query = qb
            .bool()
                .must(qb.keyword().onFields("subject", "contentText", "contentHtml").matching(searchExpression).createQuery())
                .must(qb.keyword().onField("user.id").matching(userId.toString()).createQuery())
            .createQuery();

        /* wrap Lucene query in a javax.persistence.Query */
        org.hibernate.search.jpa.FullTextQuery persistenceQuery = fullTextEntityManager.createFullTextQuery(query, MessagePO.class);

        /* add sort order */
        persistenceQuery.setSort(new Sort(new SortField("dateReceived", SortField.LONG, true)));

        /* execute search */
        List result = persistenceQuery
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .getResultList();

        return result;
    }

    public Long searchForMessagesTotalCount(final Long userId, final String searchExpression) {

        final FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
        final QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(MessagePO.class).get();
        org.apache.lucene.search.Query query = qb
            .bool()
                .must(qb.keyword().onFields("subject", "contentText", "contentHtml").ignoreFieldBridge().matching(searchExpression).createQuery())
                .must(qb.keyword().onField("user.id").matching(userId.toString()).createQuery())
            .createQuery();
        /* wrap Lucene query in a javax.persistence.Query */

        // TODO: sort by date

        return new Long(fullTextEntityManager.createFullTextQuery(query, MessagePO.class).getResultSize());
    }

	public MessagePO getMessageByIdAndUser(final Long messageId, final Long userId) {
		return em.createQuery("from MessagePO where id = ?1 and user.id = ?2", MessagePO.class)
			.setParameter(1, messageId)
			.setParameter(2, userId)
			.getSingleResult();
	}

    public AttachmentPO getAttachmentById(final Long attachmentId, final Long userId) {
        return em.createQuery("from AttachmentPO where id = :id and user.id = :userId", AttachmentPO.class)
            .setParameter("id", attachmentId)
            .setParameter("userId", userId)
            .getSingleResult();

    }

    public int deleteMessagesForTagLineage(final Long userId, final Collection<Long> msgUids, Long tagLineageId) {
        // Workaround
        // TODO: get cascading to work with batch deletes
        final List<Long> msgIds = em.createQuery("select message.id from MessageTagLineageMappingPO where msgUid in :msgIds and tagLineage.id = :tagLineageId", Long.class)
                .setParameter("msgIds", msgUids)
                .setParameter("tagLineageId", tagLineageId)
                .getResultList();

        return deleteMessages(userId, msgIds);
    }

    public int deleteMessages(final Long userId, final List<Long> messageIds) {
        final int deletedTagLineageMappings = em.createQuery("delete from MessageTagLineageMappingPO where message.id in :msgIds")
                .setParameter("msgIds", messageIds)
                .executeUpdate();
        if(logger.isDebugEnabled()) {
            logger.debug("Deleted " + deletedTagLineageMappings + " MessageTagLineageMappings.");
        }

        final int deletedAttachments = em.createQuery("delete from AttachmentPO where message.id in :msgIds and user.id = :userId")
                .setParameter("msgIds", messageIds)
                .setParameter("userId", userId)
                .executeUpdate();
        if(logger.isDebugEnabled()) {
            logger.debug("Deleted " + deletedAttachments + " attachments.");
        }

        final int deletedMessages = em.createQuery("delete from MessagePO where user.id = :userId and id in :msgIds")
                .setParameter("msgIds", messageIds)
                .setParameter("userId", userId)
                .executeUpdate();
        if(logger.isDebugEnabled()) {
            logger.debug("Deleted " + deletedMessages + " messages.");
        }
        return deletedMessages;
    }

    public List<MessagePO> getMessagesByIdAndUser(final List<Long> messageIds, final Long userId, final boolean orderByAccount) {
		String sql = "from MessagePO where id IN ?1 and user.id = ?2";
		if(orderByAccount)
			sql += " order by messageAccount.id";
		return em.createQuery(sql, MessagePO.class)
			.setParameter(1, messageIds)
			.setParameter(2, userId)
			.getResultList();
	}
	
	public MessagePO getMessageFromHash(final Long userId, final Long accountId, final String hash) {
		try {
			return em.createQuery("from MessagePO where user.id = ?1 and messageAccount.id = ?2 and hash = ?3", MessagePO.class)
				.setParameter(1, userId)
				.setParameter(2, accountId)
				.setParameter(3, hash)
				.getSingleResult();
		} catch(NoResultException nre) {
			return null;
		} 				
	}
	
	public Boolean existsMessageWithHash(final Long userId, final Long accountId, final String hash) {
		try {
			em.createQuery("select 1 from MessagePO where user.id = ?1 and messageAccount.id = ?2 and hash = ?3", Integer.class)
				.setParameter(1, userId)
				.setParameter(2, accountId)
				.setParameter(3, hash)
				.getSingleResult();
				return Boolean.TRUE;
		} catch(NoResultException nre) {
			return Boolean.FALSE; 
		} 				
	}	

	public Long getMessagesForTagLineageTotalCount(final Long userId, final Long tagLineageId) {
		return  em.createQuery("select count(m.id) from MessagePO m join m.msgTagLineageMappings map where m.user.id = ?1 and map.tagLineage.id =?2", Long.class)
			.setParameter(1, userId)
			.setParameter(2, tagLineageId)
			.getSingleResult();
	}

	public List<MessagePO> getMessagesForTagLineage(final Long userId, final Long tagLineageId, final Integer firstResult, final Integer maxResults) {
		return  em.createQuery("select m from MessagePO m join m.msgTagLineageMappings map where m.user.id = ?1 and map.tagLineage.id =?2 order by m.dateReceived desc", MessagePO.class)
			.setParameter(1, userId)
			.setParameter(2, tagLineageId)
			.setFirstResult(firstResult)
			.setMaxResults(maxResults)
			.getResultList();
	}	
	
	public MessagePO getMessageByUIDAndTagLineage(final Long userId, final Long msgUid, final Long tagLineageId) {

        if(logger.isDebugEnabled()) {
            logger.debug("getMessageByUIDAndTagLineage");
        }

		try {
			return em.createQuery("select m from MessagePO m join m.msgTagLineageMappings map where m.user.id = ?1 and map.msgUid = ?2 and map.tagLineage.id =?3", MessagePO.class)
				.setParameter(1, userId)
				.setParameter(2, msgUid)
				.setParameter(3, tagLineageId)
				.getSingleResult();
		} catch(NoResultException nre) {
			return null;
		} 
	}
	
	public List<MessageAccountPO> getMessageAccounts(final Long userId) {
		return em.createQuery("from MessageAccountPO where user.id = ?1", MessageAccountPO.class)
				.setParameter(1, userId)
				.getResultList();
	}

    public List<Long> getMsgCountForLastTenDays(final Long userId) {
        return em.createNativeQuery("select count(1) from message where date_received > DATE_ADD(now(), INTERVAL -10 DAY) and user_id = ?1 group by DAY(date_received) desc")
                .setParameter(1, userId)
                .getResultList();
    }

	/**
	 * Returns the message account object with id <i>accountId</i> owned by the user with id <i>userId</i> or
	 * null if no such account exists.
	 * @param userId
	 * @param accountId
	 * @return
	 */
	public MessageAccountPO getMessageAccount(final Long userId, final Long accountId) {

		try {
			return em.createQuery("from MessageAccountPO where user.id = ?1 and id = ?2", MessageAccountPO.class)
				.setParameter(1, userId)
				.setParameter(2, accountId)
				.getSingleResult();
		} catch(NoResultException nre) {
			return null;
		} 
	}

	public EmailAddressPO getOrCreateEmailAddress(final UserPO user, final String emailAddrStr ) {
		EmailAddressPO emailAddr = getEmailAddress(user.getId(), emailAddrStr);
		if(emailAddr != null)
			return emailAddr;
		emailAddr = new EmailAddressPO();
		emailAddr.setUser(user);
		emailAddr.setEmailAddress(emailAddrStr);

		genericDAO.persist(emailAddr);
		return emailAddr;
	}

	public EmailAddressPO getEmailAddress(final Long userId, final String emailAddrStr) {
		try {
			return em.createQuery("from EmailAddressPO where user.id = ?1 and lower(emailAddress) = lower(?2)", EmailAddressPO.class)
				.setParameter(1, userId)
				.setParameter(2, emailAddrStr)
				.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
	
	public Map<Long, Boolean> getMsgUidFlagMapForTagLineage(final Long userId, final Long tagLineageId) {
		final Map<Long, Boolean> resultMap = new HashMap<Long, Boolean>();
		@SuppressWarnings("unchecked")
		List<Object[]> l = em.createQuery("select map.msgUid, m.read from MessageTagLineageMappingPO map join map.message m where m.user.id = ?1 and map.tagLineage.id = ?2")
				.setParameter(1, userId)
				.setParameter(2, tagLineageId)
				.getResultList();
		
		final Iterator<Object[]> it = l.iterator();
		while(it.hasNext()) {
			final Object[] cur = it.next();
			resultMap.put((Long)cur[0],(Boolean)cur[1]);
		}
		return resultMap;
	}

    public void addTagLineage(final MessagePO message, final TagLineagePO tagLineage, final Long messageUid) {
        if(message.getMsgTagLineageMappings().contains(tagLineage))
            return;
        final MessageTagLineageMappingPO mapping = new MessageTagLineageMappingPO();
        mapping.setMessage(message);
        mapping.setTagLineage(tagLineage);
        mapping.setMsgUid(messageUid);
        genericDAO.persist(mapping);
        message.getMsgTagLineageMappings().add(mapping);
    }

}
