package net.mortalsilence.indiepim.server.dao;

import net.mortalsilence.indiepim.server.domain.ContactPO;
import net.mortalsilence.indiepim.server.dto.EmailAddressDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 12.11.12
 * Time: 21:30
 * To change this template use File | Settings | File Templates.
 */
@Named
public class ContactDAO {

    @PersistenceContext
   	private EntityManager em;
    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");

    public ContactPO getSingleContactByOrigin(final String origin, final Long userId) {
   		try {
   			return em.createQuery("from ContactPO where origin = ?1 and user.id = ?2", ContactPO.class)
   				.setParameter(1, origin)
   				.setParameter(2, userId)
   				.getSingleResult();
   		} catch (NoResultException nre) {
   			return null;
   		}
   	}

    public List<EmailAddressDTO> getAllEmailAddresses(Long userId) {
        final List<EmailAddressDTO> result = new LinkedList<EmailAddressDTO>();
        @SuppressWarnings("unchecked")
        List<Object[]> l = em.createQuery("select e.id, e.emailAddress, c.displayName from EmailAddressPO e left outer join e.contact c where e.user.id = ?1")
                .setParameter(1, userId)
                .getResultList();

        final Iterator<Object[]> it = l.iterator();
        while(it.hasNext()) {
            final Object[] cur = it.next();
            result.add(new EmailAddressDTO((Long)cur[0], (String)cur[1], (String) cur[2]));
        }
        return result;
    }

    public List<EmailAddressDTO> searchForEmailAddresses(final Long userId, final String query) {
        final List<EmailAddressDTO> result = new LinkedList<EmailAddressDTO>();
        @SuppressWarnings("unchecked")
        List<Object[]> l = em.createQuery("select e.id, e.emailAddress, c.displayName from EmailAddressPO e left outer join e.contact c where e.user.id = ?1 and (e.emailAddress like ?2 or c.displayName like ?2)")
                .setParameter(1, userId)
                .setParameter(2, "%" + query + "%")
                .getResultList();

        final Iterator<Object[]> it = l.iterator();
        while(it.hasNext()) {
            final Object[] cur = it.next();
            result.add(new EmailAddressDTO((Long)cur[0], (String)cur[1], (String) cur[2]));
        }
        return result;
    }

    public List<ContactPO> getContactsForTagLineage(Long userId, Long tagLineageId, Integer offset, Integer pageSize) {
        return em.createQuery("select c from ContactPO c join c.tagLineages tl where c.user.id = ?1 and tl.id = ?2 order by c.displayName", ContactPO.class)
                .setParameter(1, userId)
                .setParameter(2, tagLineageId)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public Long getContactsForTagLineageCount(Long userId, Long tagLineageId) {
        return em.createQuery("select count(c.id) from ContactPO c join c.tagLineages tl where c.user.id = ?1 and tl.id = ?2", Long.class)
                .setParameter(1, userId)
                .setParameter(2, tagLineageId)
                .getSingleResult();
    }

    public List<ContactPO> getContactsForTag(Long userId, String tagName, Integer offset, Integer pageSize) {
        return em.createNativeQuery("select c.* from contact_tag_view v, tag t, contact c where v.user_id = ?1 and t.id = v.tag_id and t.tag = ?2 and c.id = v.contact_id order by display_name", ContactPO.class)
                .setParameter(1, userId)
                .setParameter(2, tagName)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public Long getContactsForTagTotalCount(Long userId, String tagName) {
        return  ((BigInteger)em.createNativeQuery("select count(*) from contact_tag_view v, tag t, contact c where v.user_id = ?1 and t.id = v.tag_id and t.tag = ?2 and c.id = v.contact_id")
                                .setParameter(1, userId)
     			                .setParameter(2, tagName)
     			                .getSingleResult()).longValue();
    }



    public List<ContactPO> searchForContacts(Long userId, String searchTerm, Integer offset, Integer pageSize) {

        return em.createQuery("from ContactPO where user.id = ?1 and (givenName like ?2 or familyName like ?2 or displayName like ?2) order by displayName", ContactPO.class)
                .setParameter(1, userId)
                .setParameter(2, searchTerm)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public Long searchForMessagesTotalCount(Long userId, String searchTerm) {
        return em.createQuery("select count(*) from ContactPO where user.id = ?1 and (givenName like ?2 or familyName like ?2 or displayName like ?2)", Long.class)
                .setParameter(1,userId)
                .setParameter(2, searchTerm)
                .getSingleResult();
    }

    public List<ContactPO> getAllContacts(Long userId, Integer offset, Integer pageSize) {
        return em.createQuery("from ContactPO where user.id = ?1 order by displayName", ContactPO.class)
                .setParameter(1, userId)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

    public Long getAllContactsCount(Long userId) {
        return em.createQuery("select count(*) from ContactPO where user.id = ?1", Long.class)
                .setParameter(1, userId)
                .getSingleResult();
    }
}
