package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.security.IndieUser;
import org.springframework.security.core.context.SecurityContextHolder;

public class ActionUtils {

	public static Long getUserId() {
        final IndieUser indieUser = (IndieUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(indieUser == null || indieUser.getId() == null)
            throw new RuntimeException("No user id");
		return indieUser.getId();
	}
}
