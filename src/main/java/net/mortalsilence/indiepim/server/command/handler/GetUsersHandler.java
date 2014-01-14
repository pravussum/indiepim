package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.GetTags;
import net.mortalsilence.indiepim.server.command.actions.GetUsers;
import net.mortalsilence.indiepim.server.command.results.TagDTOListResult;
import net.mortalsilence.indiepim.server.command.results.UserDTOListResult;
import net.mortalsilence.indiepim.server.dao.TagDAO;
import net.mortalsilence.indiepim.server.dao.UserDAO;
import net.mortalsilence.indiepim.server.domain.TagPO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import net.mortalsilence.indiepim.server.utils.TagUtils;
import net.mortalsilence.indiepim.server.utils.UserUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collection;

@Service
public class GetUsersHandler implements Command<GetUsers, UserDTOListResult> {

    @Inject
    private UserDAO userDAO;

    @Transactional(readOnly = true)
	@Override
    public UserDTOListResult execute(GetUsers action) {

        final Collection<UserPO> userPOs = userDAO.getUsers();
        return new UserDTOListResult(UserUtils.mapUserPOs2UserDTOs(userPOs));
	}

	@Override
	public void rollback(GetUsers arg0, UserDTOListResult arg1) {
		// no use rolling back a getter
	}

}
