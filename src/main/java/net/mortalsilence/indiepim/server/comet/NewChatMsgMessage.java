package net.mortalsilence.indiepim.server.comet;

public class NewChatMsgMessage implements CometMessage {

	private Long fromUserId;
    private String fromUserName;
	private String message;

	public Long getFromUserId() {
		return fromUserId;
	}
	public void setFromUserId(Long fromUserId) {
		this.fromUserId = fromUserId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
    public String getFromUserName() { return fromUserName;  }
    public void setFromUserName(String fromUserName) { this.fromUserName = fromUserName; }

    public NewChatMsgMessage(final Long fromUserId, final String fromUserName, final  String message) {
		super();
		this.fromUserId = fromUserId;
		this.fromUserName = fromUserName;
        this.message = message;
	}
	public NewChatMsgMessage() {

	}

    @Override
    public String getMessageType() {
        return "NewChatMessage";
    }
}
