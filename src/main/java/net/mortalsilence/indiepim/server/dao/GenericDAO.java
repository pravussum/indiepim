package net.mortalsilence.indiepim.server.dao;

import net.mortalsilence.indiepim.server.domain.PersistentObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Named
public class GenericDAO {
	
    @PersistenceContext
    private EntityManager em;
    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");

	
	public void updateOrPersist(final PersistentObject object) {
		if(object.getId() != null)
			update(object);
		else 
			persist(object);
	}
	
	public <T> void persist(final T object) {
		em.persist(object);
	}
	
	public<T> void update(final T object) {
		em.merge(object);
	}
	
	public <T> void remove(final T object) {
		em.remove(object);
	}
	
	public <T> void refresh (final T object){
		em.refresh(object);
	}
	
	public <T> boolean isAttached (final T object){
		return em.contains(object);
	}
	
	public <T> T merge(final T object) {
		return em.merge(object);
	}

    public void flush() {
        em.flush();
    }
	
	/**
	 * @deprecated Use one of the specialized DAOs and strictly check with user id parameter!!
	 */
	public <T> T getObject(final Long id, final Class<T> c) {
		return  em.find(c, id);				
	}
}
