package net.mortalsilence.indiepim.server.comet;

public class UserOnlineStateMessage implements CometMessage {

	private Long userId;
    private Boolean online;
    private String userName;

    public UserOnlineStateMessage() {
    }

    public UserOnlineStateMessage(final Long userId, final Boolean online, String userName) {
        this.userId = userId;
        this.online = online;
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(final Boolean online) {
        this.online = online;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getMessageType() {
        return "UserOnlineStateMessage";
    }
}
