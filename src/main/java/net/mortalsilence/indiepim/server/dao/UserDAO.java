package net.mortalsilence.indiepim.server.dao;

import net.mortalsilence.indiepim.server.domain.UserPO;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.Collections;


@Named
public class UserDAO {

    @PersistenceContext
	private EntityManager em;
    @Inject
    private GenericDAO genericDAO;
    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");

	/**
	 * Returns the user object for the given username or null if the user does not exist.
	 * @param userName
	 * @return
	 */
	public UserPO getByUsername(final String userName) {
		try {
			return em.createQuery("from UserPO where userName = ?1", UserPO.class)
					.setParameter(1,userName)
					.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	public UserPO getUser(final Long id) {
		return genericDAO.getObject(id, UserPO.class);
	}

    public Collection<UserPO> getUsers() {
        return em.createQuery("from UserPO", UserPO.class).getResultList();
    }

    public Collection<UserPO> getUsers(final Collection<Long> ids) {
        if(ids == null || ids.isEmpty())
            return Collections.EMPTY_LIST;
        return em.createQuery("from UserPO where id in :ids", UserPO.class)
                .setParameter("ids", ids)
                .getResultList();
    }

	public boolean userExists(final String userName) {
		return em.createQuery("select count(user) from UserPO user where userName = ?1", Long.class)
			.setParameter(1, userName)
			.getSingleResult() > 0;
	}
	
}
