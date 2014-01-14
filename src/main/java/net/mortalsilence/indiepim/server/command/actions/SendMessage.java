package net.mortalsilence.indiepim.server.command.actions;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.IdResult;

import java.util.List;

public class SendMessage extends AbstractSessionAwareAction<IdResult> {

	@JsonProperty("accountId")  private Long accountId;
    @JsonProperty("subject")    private String subject;
    @JsonProperty("to")         private List<String> receiver;
    @JsonProperty("cc")         private List<String> cc;
    @JsonProperty("bcc")        private List<String> bcc;
    @JsonProperty("content")        private String content;
    @JsonProperty("isHtml")         private Boolean isHtml;

    public SendMessage(Long accountId, String subject, List<String> receiver, List<String> cc, List<String> bcc, String content, Boolean html) {
        this.accountId = accountId;
        this.subject = subject;
        this.receiver = receiver;
        this.cc = cc;
        this.bcc = bcc;
        this.content = content;
        isHtml = html;
    }

    public SendMessage() {
	}

	public Long getAccountId() {
		return accountId;
	}

    public String getSubject() {
        return subject;
    }

    public List<String> getReceiver() {
        return receiver;
    }

    public List<String> getCc() {
        return cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public String getContent() {
        return content;
    }

    public Boolean isHtml() {
        return isHtml;
    }
}
