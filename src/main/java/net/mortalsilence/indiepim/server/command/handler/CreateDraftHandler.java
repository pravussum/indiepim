package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.CreateDraft;
import net.mortalsilence.indiepim.server.command.actions.GetMessage;
import net.mortalsilence.indiepim.server.command.exception.CommandException;
import net.mortalsilence.indiepim.server.command.results.CreateDraftResult;
import net.mortalsilence.indiepim.server.command.results.MessageDTOResult;
import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.dao.UserDAO;
import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import net.mortalsilence.indiepim.server.domain.MessagePO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import net.mortalsilence.indiepim.server.dto.MessageDTO;
import net.mortalsilence.indiepim.server.exception.UserRuntimeException;
import net.mortalsilence.indiepim.server.utils.MessageUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import java.util.List;

@Named
public class CreateDraftHandler implements Command<CreateDraft, CreateDraftResult> {

    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");

    @Inject private MessageDAO messageDAO;
    @Inject private MessageUtils messageUtils;
    @Inject private GenericDAO genericDAO;
    @Inject private UserDAO userDAO;

	@Transactional (readOnly = false)
    @Override
    public CreateDraftResult execute(CreateDraft action) throws CommandException {
        final UserPO user = userDAO.getUser(ActionUtils.getUserId());
            final List<MessageAccountPO> accounts = messageDAO.getMessageAccounts(ActionUtils.getUserId());
            final CreateDraftResult result = new CreateDraftResult();

            if(accounts == null || accounts.isEmpty())
                throw new UserRuntimeException("No message accounts configured. You must create a message account first.");
            MessagePO draft = messageDAO.getDraft(ActionUtils.getUserId(), action.getOrigMessageId());

            if(draft == null) {
                if(logger.isDebugEnabled())
                    logger.debug("Creating new draft...");
                draft = new MessagePO();
                draft.setMessageAccount(accounts.get(0)); // will be overwritten during automatic draft update or when sending
                draft.setUser(user);
                draft.setDraft(true);
                genericDAO.persist(draft);
            } else {
                if(logger.isDebugEnabled())
                    logger.debug("Found existing draft with id" + draft.getId());
                result.draft = messageUtils.mapMessagePOtoMessageDTO(draft);
            }

            result.id = draft.getId();
            if(action.getOrigMessageId() != null) {
                final MessagePO origMessage = messageDAO.getMessageByIdAndUser(action.getOrigMessageId(), ActionUtils.getUserId());
                final MessageDTO origMessageDTO = messageUtils.mapMessagePOtoMessageDTO(origMessage);
                if(logger.isDebugEnabled())
                    logger.debug("Found original message with id " + origMessage.getId());
                draft.setRelatedMessage(messageDAO.getMessageByIdAndUser(action.getOrigMessageId(), ActionUtils.getUserId()));
                result.origMessage = origMessageDTO;
            }
            return result;
	}

	@Override
	public void rollback(CreateDraft arg0, CreateDraftResult arg1) {
		// no use rolling back a getter
	}

}
