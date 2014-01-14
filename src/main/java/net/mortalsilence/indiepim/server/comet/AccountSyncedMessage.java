package net.mortalsilence.indiepim.server.comet;

import java.io.Serializable;
import java.util.Date;

public class AccountSyncedMessage implements CometMessage {

	private Long userId;
	private Long accountId;
	private Date syncDate;
	
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
	public AccountSyncedMessage(Long userId, Long accountId, Date syncDate) {
		super();
		this.userId = userId;
		this.accountId = accountId;
		this.syncDate = syncDate;
	}
	public AccountSyncedMessage() {
	}
	public Date getSyncDate() {
		return syncDate;
	}
	public void setSyncDate(Date syncDate) {
		this.syncDate = syncDate;
	}

    @Override
    public String getMessageType() {
        return "AccountSynced";
    }
}
