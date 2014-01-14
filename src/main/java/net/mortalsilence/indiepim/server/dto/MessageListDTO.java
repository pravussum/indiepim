package net.mortalsilence.indiepim.server.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.format.ISODateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;


public class MessageListDTO implements Serializable {

    @JsonIgnore public Date dateReceived;
    @JsonProperty("dateReceived") public String getDateReceived() {
        return ISODateTimeFormat.dateTime().print(dateReceived.getTime());
    }

	public String sender;
	public  String subject;
	public Long msgId;
	public Boolean read;
	public Set<TagDTO> tags = new TreeSet<TagDTO>();
	public Boolean hasAttachment;
	public String contentPreview;
	
	public void update(MessageListDTO msgDTO) {
		this.dateReceived = msgDTO.dateReceived;
		this.sender = msgDTO.sender;
		this.subject = msgDTO.subject;
		this.msgId = msgDTO.msgId;
		this.read = msgDTO.read;
		this.tags = msgDTO.tags;
		this.hasAttachment = msgDTO.hasAttachment;
	}
}
