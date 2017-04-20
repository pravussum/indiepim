package net.mortalsilence.indiepim.server.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "attachment")
@SuppressWarnings("serial")
public class AttachmentPO implements Serializable, PersistentObject {
	@Id
    @GeneratedValue
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private UserPO user;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="message_id", referencedColumnName="id")
	private MessagePO message;
	
	@Column(name = "filename")
	private String filename;
	
	@Column(name = "mime_type")
	private String mimeType;
	
	@Column(name = "pos_no")
	private Integer posNo;
	
	@Column(name = "disposition")
	private String disposition;

    @Column(name = "tempfilename")
    private String tempFilename;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserPO getUser() {
		return user;
	}

	public void setUser(UserPO user) {
		this.user = user;
	}

	public MessagePO getMessage() {
		return message;
	}

	public void setMessage(MessagePO message) {
		if(this.message != null)
			this.message.getAttachments().remove(this);
		this.message = message;
		this.message.getAttachments().add(this);
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Integer getPosNo() {
		return posNo;
	}

	public void setPosNo(Integer posNo) {
		this.posNo = posNo;
	}

	public String getDisposition() {
		return disposition;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}

    public String getTempFilename() {
        return tempFilename;
    }

    public void setTempFilename(String tempFilename) {
        this.tempFilename = tempFilename;
    }
}
