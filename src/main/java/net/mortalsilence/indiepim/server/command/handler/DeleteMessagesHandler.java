package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.DeleteMessages;
import net.mortalsilence.indiepim.server.command.results.BooleanResult;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.domain.MessagePO;
import net.mortalsilence.indiepim.server.message.MessageUpdateService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.mail.Flags;
import java.util.LinkedList;
import java.util.List;

@Service
public class DeleteMessagesHandler implements Command<DeleteMessages, BooleanResult> {

    @Inject
    private MessageUpdateService messageUpdateService;
    @Inject
    private MessageDAO messageDAO;

    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");

	@Transactional
    @Override
    public BooleanResult execute(DeleteMessages action) {
		final List<Long> messageIds = action.getMessageIds();
        final Long userId = ActionUtils.getUserId();
        final List<MessagePO> messages = messageDAO.getMessagesByIdAndUser(messageIds, userId, true);
		List<MessagePO> accountMessages = new LinkedList<MessagePO>();
		
		if(messages.isEmpty())
			return new BooleanResult(false);
		Long accountId = messages.get(0).getMessageAccount().getId();
		int i=0;
		for(final MessagePO curMsg : messages){
			if(accountId.equals(curMsg.getMessageAccount().getId()) && i < messages.size() -1) {
				accountMessages.add(curMsg);
			} else {
				// TODO give accountMessages and not messages as parameter 
                final long start = System.currentTimeMillis();
                final List<MessagePO> result = messageUpdateService.setImapFlagForMessages(userId, messages, accountId, Flags.Flag.DELETED, true);
                logger.debug("messageUpdateService.setImapFlagForMessages (delete) took " + (System.currentTimeMillis() - start) + " ms.");

                accountId = curMsg.getMessageAccount().getId();
                accountMessages = new LinkedList<MessagePO>();

                final List<Long> deleteIds = new LinkedList<Long>();
                for(final MessagePO succMsg : result) {
                    deleteIds.add(succMsg.getId());
                }
                messageDAO.deleteMessages(userId, deleteIds);
			}
			i++;
		}
		return new BooleanResult(true);
	}


	@Override
	public void rollback(DeleteMessages arg0, BooleanResult arg1) {
		// TODO how to roll back?
	}

}
