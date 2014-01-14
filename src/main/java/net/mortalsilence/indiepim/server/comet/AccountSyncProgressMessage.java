package net.mortalsilence.indiepim.server.comet;

public class AccountSyncProgressMessage implements CometMessage {

	private Long userId;
	private Long accountId;
	private String folder;
	private Integer msgCount;
	private Integer msgDoneCount;
	
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
	
	public AccountSyncProgressMessage(Long userId, Long accountId, String folder, Integer msgCount, Integer msgDoneCount) {
		super();
		this.userId = userId;
		this.accountId = accountId;
		this.msgCount = msgCount;
		this.msgDoneCount = msgDoneCount;
		this.folder = folder;
	}
	
	public AccountSyncProgressMessage() {
	}
	
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	public Integer getMsgCount() {
		return msgCount;
	}
	public void setMsgCount(Integer msgCount) {
		this.msgCount = msgCount;
	}
	public Integer getMsgDoneCount() {
		return msgDoneCount;
	}
	public void setMsgDoneCount(Integer msgDoneCount) {
		this.msgDoneCount = msgDoneCount;
	}

    @Override
    public String getMessageType() {
        return "AccountSyncProgress";
    }
}
