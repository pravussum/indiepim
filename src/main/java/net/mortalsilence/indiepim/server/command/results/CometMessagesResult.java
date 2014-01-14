package net.mortalsilence.indiepim.server.command.results;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.mortalsilence.indiepim.server.comet.CometMessage;
import net.mortalsilence.indiepim.server.command.Result;

import java.util.Collection;

public class CometMessagesResult implements Result {

	@JsonProperty("cometMessages") private Collection<CometMessage> cometMessages;
    @JsonProperty("isHeartbeat") private boolean isHeartbeat;
    @JsonProperty("isError") private boolean isError;
    @JsonProperty("errorMessage") private String errorMessage;

    @JsonIgnore
    public boolean isHeartbeat() {
		return isHeartbeat;
	}

	public void setHeartbeat(boolean isHeartbeat) {
		this.isHeartbeat = isHeartbeat;
	}

    @JsonIgnore
    public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

    @JsonIgnore
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

    @JsonIgnore
	public CometMessagesResult(Collection<CometMessage> messages) {
		super();
		this.cometMessages = messages;
	}

	public CometMessagesResult() {
	}

    @JsonIgnore
    public Collection<CometMessage> getCometMessages() {
        return cometMessages;
    }

    public void setCometMessages(Collection<CometMessage> cometMessages) {
        this.cometMessages = cometMessages;
    }
}
