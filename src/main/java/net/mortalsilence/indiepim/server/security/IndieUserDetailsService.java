package net.mortalsilence.indiepim.server.security;

import net.mortalsilence.indiepim.server.dao.UserDAO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;

@Service
public class IndieUserDetailsService implements UserDetailsService {

    @Inject
    private UserDAO userDAO;

    @Override
    @Transactional(readOnly = true)
    public IndieUser loadUserByUsername(String s) throws UsernameNotFoundException {
        final UserPO user = userDAO.getByUsername(s);
        if (user == null)
            throw new UsernameNotFoundException("User " + s + " not found.");

        final Collection<GrantedAuthority> grants = new LinkedList<GrantedAuthority>();
        grants.add(new SimpleGrantedAuthority("ROLE_USER"));
        if(user.isAdmin()) {
            grants.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        final IndieUser indieUser = new IndieUser(user.getUserName(), user.getPasswordHash(), grants);
        indieUser.setId(user.getId());
        return indieUser;
    }
}