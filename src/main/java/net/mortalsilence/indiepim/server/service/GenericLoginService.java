package net.mortalsilence.indiepim.server.service;

import net.mortalsilence.indiepim.server.dao.ConfigDAO;
import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.dao.UserDAO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import net.mortalsilence.indiepim.server.thirdparty.BCrypt;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 16.01.13
 * Time: 20:12
 * To change this template use File | Settings | File Templates.
 */
@Service
public class GenericLoginService {

    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");
    @Inject
    private UserDAO userDAO;
    @Inject
    private ConfigDAO configDAO;
    @Inject
    private GenericDAO genericDAO;


    @Transactional
    public void addUser(final String userName, final String password) {
   		if(userName == null || password == null)
   			throw new IllegalArgumentException("Username and password must not be empty.");

   			if(userDAO.userExists(userName))
   				throw new RuntimeException("Username already used.");
   			UserPO newUser = new UserPO();
   			newUser.setUserName(userName);
   			newUser.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
   			genericDAO.persist(newUser);

   			return;
   	}
}