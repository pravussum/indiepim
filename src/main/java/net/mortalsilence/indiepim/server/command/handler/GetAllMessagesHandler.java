package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.GetMessages;
import net.mortalsilence.indiepim.server.command.results.MessageListResult;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.domain.MessagePO;
import net.mortalsilence.indiepim.server.dto.MessageListDTO;
import net.mortalsilence.indiepim.server.utils.MessageUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;
import java.util.List;

@Named
public class GetAllMessagesHandler implements Command<GetMessages, MessageListResult> {

	final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");
    @Inject
    private MessageDAO messageDAO;
    @Inject
    private MessageUtils messageUtils;
    @Inject
    SessionRegistry sessionRegistry;

    @Transactional (readOnly = true)
	@Override
    public MessageListResult execute(GetMessages action) {
		final Long userId = ActionUtils.getUserId();
		final List<MessagePO> messages;
		Long count;
		final Long start = Calendar.getInstance().getTimeInMillis();
		final Long queryTime;
		if (action.getAccountId() != null) {
			messages = messageDAO.getMessagesForAccount(userId, action.getAccountId(), action.getOffset(), action.getPageSize());
			queryTime = Calendar.getInstance().getTimeInMillis();
			count = messageDAO.getMessagesForAccountTotalCount(userId, action.getAccountId());
		}
		else if(action.getTagLineageId() != null) {
			messages = messageDAO.getMessagesForTagLineage(userId, action.getTagLineageId(), action.getOffset(), action.getPageSize());
			queryTime = Calendar.getInstance().getTimeInMillis();
			count = messageDAO.getMessagesForTagLineageTotalCount(userId, action.getTagLineageId());
		}
		else if (action.getTagName() != null && !"".equals(action.getTagName())) {
			messages = messageDAO.getMessagesForTag(ActionUtils.getUserId(), action.getTagName(), action.getOffset(), action.getPageSize());
			queryTime = Calendar.getInstance().getTimeInMillis();
			count = messageDAO.getMessagesForTagTotalCount(userId, action.getTagName());
		}
		else if(action.getSearchTerm() != null && !"".equals(action.getSearchTerm())) {
            // TODO check input parameters !!!
            messages = messageDAO.searchForMessages(ActionUtils.getUserId(), action.getSearchTerm(), action.getOffset(), action.getPageSize());
            queryTime = Calendar.getInstance().getTimeInMillis();
            count = messageDAO.searchForMessagesTotalCount(userId, action.getSearchTerm());

		} else if(action.getRead() != null) {
            messages = messageDAO.getMessagesByReadFlag(ActionUtils.getUserId(), action.getRead(), action.getOffset(), action.getPageSize());
            queryTime = Calendar.getInstance().getTimeInMillis();
            count = messageDAO.getMessagesByReadFlagTotalCount(userId, action.getRead());
        }
		else {
			messages = messageDAO.getAllMessages(userId, action.getOffset(), action.getPageSize());
			queryTime = Calendar.getInstance().getTimeInMillis();
			count = messageDAO.getAllMessagesTotalCount(userId);
		} 
		final Long countTime = Calendar.getInstance().getTimeInMillis();
		logger.info("GetMessages: Query took " + (queryTime.intValue() - start.intValue()) + "ms.");
		logger.info("GetMessages: Count took " + (countTime.intValue() - queryTime.intValue()) + "ms.");
		List<MessageListDTO> result = messageUtils.mapMessagePOtoMessageDTOList(messages);
		final Long mapTime = Calendar.getInstance().getTimeInMillis();
		logger.info("GetMessages: Mapping took " + (mapTime.intValue() - countTime.intValue()) + "ms.");
		
		return new MessageListResult(result, count);
	}

	@Override
	public void rollback(GetMessages arg0, MessageListResult arg1) {
		// n/a
	}
	
	

}
