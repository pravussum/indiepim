package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.GetAttachment;
import net.mortalsilence.indiepim.server.command.actions.GetMessage;
import net.mortalsilence.indiepim.server.command.exception.CommandException;
import net.mortalsilence.indiepim.server.command.results.ByteArrayResult;
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
public class GetAttachmentHandler implements Command<GetAttachment, ByteArrayResult> {

	@Transactional (readOnly = true)
    @Override
    public ByteArrayResult execute(GetAttachment action) throws CommandException {
        // TODO implement me! Use MessageUpdateService-ish class (read-only, with callback)
        return new ByteArrayResult();
	}

	@Override
	public void rollback(GetAttachment arg0, ByteArrayResult arg1) {
		// no use rolling back a getter
	}

}
