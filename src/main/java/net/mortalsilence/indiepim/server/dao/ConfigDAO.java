package net.mortalsilence.indiepim.server.dao;

import net.mortalsilence.indiepim.server.domain.SystemConfigPO;
import net.mortalsilence.indiepim.server.domain.UserConfigPO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Named
public class ConfigDAO {
	
    @PersistenceContext
	private EntityManager em;
    @Inject
    private UserDAO userDAO;
    @Inject
    private GenericDAO genericDAO;

    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");

	
	public String getSystemPropertyValue(final String key) {
		
		try {
			return em.createQuery("select value from SystemConfigPO where key = ?1", String.class)
				.setParameter(1, key)
				.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} 		
	}
	
	public SystemConfigPO getSystemProperty(final String key) {

		try {
			return em.createQuery("from SystemConfigPO where key = ?1", SystemConfigPO.class)
							.setParameter(1, key)
							.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
		
	
	public void setSystemProperty(final String key, final String value) {
		SystemConfigPO cfg = getSystemProperty(key);
		if(cfg == null) {
			cfg = new SystemConfigPO();
			cfg.setKey(key);
			cfg.setValue(value);
			genericDAO.persist(cfg);
		} else {
			cfg.setValue(value);
			cfg = genericDAO.update(cfg);
		}
	}
	
	public String getUserPropertyValue(final Long userId, final String key) {
		
		try {
			return em.createQuery("select value from UserConfigPO where key = ?1 and user.id = ?2", String.class)
				.setParameter(1, key)
				.setParameter(2, userId)
				.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		} 		
	}
	
	public UserConfigPO getUserProperty(final Long userId, final String key) {

		try {
			return em.createQuery("from UserConfigPO where key = ?1 and user.id = ?2", UserConfigPO.class)
							.setParameter(1, key)
							.setParameter(2, userId)
							.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}
		
	
	public void setUserPropertyValue(final Long userId, final String key, final String value) {
		UserConfigPO cfg = getUserProperty(userId, key);
		if(cfg == null) {
			cfg = new UserConfigPO();
			cfg.setUser(userDAO.getUser(userId));
			cfg.setKey(key);
			cfg.setValue(value);
			genericDAO.persist(cfg);
		} else {
			cfg.setValue(value);
			genericDAO.update(cfg);
		}
	}
	
	public void deleteUserProperty(final Long userId, final String key) {
		UserConfigPO cfg = getUserProperty(userId, key);
		genericDAO.remove(cfg);
	}
}
