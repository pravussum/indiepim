package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.comet.CometService;
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
import java.util.Set;

@Service
public class GetUsersHandler implements Command<GetUsers, UserDTOListResult> {

    @Inject private UserDAO userDAO;
    @Inject private CometService cometService;

    @Transactional(readOnly = true)
	@Override
    public UserDTOListResult execute(GetUsers action) {

        final Long userId = ActionUtils.getUserId();
        final Collection<UserPO> userPOs;
        if(action.getOnlineOnly()) {
            /* get users only, that have/had a browser session running within the last minute */
            final Set<Long> onlineUserIds = cometService.getOnlineUsers(userId);
            onlineUserIds.remove(userId); // remove own user id
            userPOs = userDAO.getUsers(onlineUserIds);
        } else {
            userPOs = userDAO.getUsers();

        }
        return new UserDTOListResult(UserUtils.mapUserPOs2UserDTOs(userPOs));
	}

	@Override
	public void rollback(GetUsers arg0, UserDTOListResult arg1) {
		// no use rolling back a getter
	}

}
