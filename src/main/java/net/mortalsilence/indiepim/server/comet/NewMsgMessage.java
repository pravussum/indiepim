package net.mortalsilence.indiepim.server.comet;

import java.io.Serializable;

public class NewMsgMessage implements CometMessage {

	private Long userId;
	private Long accountId;

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public NewMsgMessage(Long userId, Long accountId) {
		super();
		this.userId = userId;
		this.accountId = accountId;
	}
	public NewMsgMessage() {

	}

    @Override
    public String getMessageType() {
        return "NewMessage";
    }
}
