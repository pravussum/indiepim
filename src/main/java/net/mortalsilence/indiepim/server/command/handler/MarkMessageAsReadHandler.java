package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.MarkMessagesAsRead;
import net.mortalsilence.indiepim.server.command.results.MessageDTOListResult;
import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.domain.MessagePO;
import net.mortalsilence.indiepim.server.dto.MessageDTO;
import net.mortalsilence.indiepim.server.message.MessageUpdateService;
import net.mortalsilence.indiepim.server.utils.MessageUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.mail.Flags;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

@Service
public class MarkMessageAsReadHandler implements Command<MarkMessagesAsRead, MessageDTOListResult> {

    @Inject
    private MessageUpdateService messageUpdateService;
    @Inject
    private MessageDAO messageDAO;
    @Inject
    private GenericDAO genericDAO;
    @Inject
    private MessageUtils messageUtils;

    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");

	@Transactional
    @Override
    public MessageDTOListResult execute(MarkMessagesAsRead action) {
		final List<Long> messageIds = action.getMessageIds();
        final Long userId = ActionUtils.getUserId();
        final List<MessagePO> messages = messageDAO.getMessagesByIdAndUser(messageIds, userId, true);
		final List<MessageDTO> resultList = new LinkedList<MessageDTO>();
		List<MessagePO> accountMessages = new LinkedList<MessagePO>();
		
		if(messages.isEmpty())
			return new MessageDTOListResult(null);
		Long accountId = messages.get(0).getMessageAccount().getId();
		int i=0;
		for(final MessagePO curMsg : messages){
			if(accountId.equals(curMsg.getMessageAccount().getId()) && i < messages.size() -1) {
				accountMessages.add(curMsg);
			} else {
				// TODO give accountMessages and not messages as parameter 
                final long start = Calendar.getInstance().getTimeInMillis();
                final List<MessagePO> result = messageUpdateService.setImapFlagForMessages(userId, messages, accountId, Flags.Flag.SEEN, action.isRead());
                logger.debug("messageUpdateService.setImapFlagForMessages took " + (Calendar.getInstance().getTimeInMillis() - start) + " ms.");

                for(final MessagePO succMsg : result) {
					succMsg.setRead(action.isRead());
					genericDAO.update(succMsg);
					resultList.add(messageUtils.mapMessagePOtoMessageDTO(succMsg));
				}
				accountId = curMsg.getMessageAccount().getId();
				accountMessages = new LinkedList<MessagePO>();
			}
			i++;
		}
		return new MessageDTOListResult(resultList);
	}


	@Override
	public void rollback(MarkMessagesAsRead arg0, MessageDTOListResult arg1) {
		// TODO how to roll back?
	}

}
