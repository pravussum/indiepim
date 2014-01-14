package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.SendMessage;
import net.mortalsilence.indiepim.server.command.results.IdResult;
import net.mortalsilence.indiepim.server.message.SendService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
public class SendMessageHandler implements Command<SendMessage, IdResult> {

    @Inject SendService sendService;

    @Transactional
	@Override
    public IdResult execute(SendMessage action) {
		// TODO validate input
		return new IdResult(
                sendService.sendMessage(
                        ActionUtils.getUserId(),
						action.getAccountId(), 
						action.getSubject(),
						action.getReceiver(),
						action.getCc(),
						action.getBcc(),
						action.getContent(),
						action.isHtml())
				);
	}

    @Override
	public void rollback(SendMessage arg0, IdResult arg1) {
		// TODO how to rollback sending an email? Delay the send?
	}

}
