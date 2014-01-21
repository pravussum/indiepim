package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.comet.CometService;
import net.mortalsilence.indiepim.server.comet.NewChatMsgMessage;
import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.SendChatMessage;
import net.mortalsilence.indiepim.server.command.results.BooleanResult;
import net.mortalsilence.indiepim.server.dao.UserDAO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
public class SendChatMessageHandler implements Command<SendChatMessage, BooleanResult> {

    @Inject CometService cometService;
    @Inject UserDAO userDAO;

    @Transactional
	@Override
    public BooleanResult execute(SendChatMessage action) {
        final Long myUserId = ActionUtils.getUserId();
        final UserPO me = userDAO.getUser(myUserId);
        return new BooleanResult(
            cometService.sendCometMessages(action.getUserId(), new NewChatMsgMessage(myUserId, me.getUserName(), action.getMessage()))
        );
	}

    @Override
	public void rollback(SendChatMessage arg0, BooleanResult arg1) {
		// TODO how to rollback?
	}

}
