package net.mortalsilence.indiepim.server.command.actions;


import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.BooleanResult;

import java.io.OutputStream;

public class GetAttachment extends AbstractSessionAwareAction<BooleanResult> {

	private Long attachmentId;
    private OutputStream outputStream;

	public GetAttachment(final Long attachmentId, final OutputStream outputStream) {
		this.attachmentId = attachmentId;
        this.outputStream = outputStream;
	}

	public Long getAttachmentId() {
		return attachmentId;
	}

	public GetAttachment() {
	}

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
