package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.ByteArrayResult;
import net.mortalsilence.indiepim.server.command.results.MessageDTOResult;

public class GetAttachment extends AbstractSessionAwareAction<ByteArrayResult> {

	private Long attachmentId;

	public GetAttachment(Long attachmentId) {
		this.attachmentId = attachmentId;
	}

	public Long getAttachmentId() {
		return attachmentId;
	}

	public GetAttachment() {
	}	
}
