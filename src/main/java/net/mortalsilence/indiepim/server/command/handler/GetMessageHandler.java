package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.GetMessage;
import net.mortalsilence.indiepim.server.command.exception.CommandException;
import net.mortalsilence.indiepim.server.command.results.ErrorResult;
import net.mortalsilence.indiepim.server.command.results.MessageDTOResult;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.domain.MessagePO;
import net.mortalsilence.indiepim.server.dto.MessageDTO;
import net.mortalsilence.indiepim.server.utils.MessageUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;

@Named
public class GetMessageHandler implements Command<GetMessage, MessageDTOResult> {

    @Inject
    private MessageDAO messageDAO;
    @Inject
    private MessageUtils messageUtils;

	@Transactional (readOnly = true)
    @Override
    public MessageDTOResult execute(GetMessage action) throws CommandException {
		final Long messageId = action.getMessageId();
		try {
            final MessagePO message = messageDAO.getMessageByIdAndUser(messageId, ActionUtils.getUserId());
            final MessageDTO messageDTO = messageUtils.mapMessagePOtoMessageDTO(message);
            return new MessageDTOResult(messageDTO);
        } catch (NoResultException nre) {
            throw new CommandException("Message with id " + messageId + " not found.");
        }


	}

	@Override
	public void rollback(GetMessage arg0, MessageDTOResult arg1) {
		// no use rolling back a getter
	}

}
