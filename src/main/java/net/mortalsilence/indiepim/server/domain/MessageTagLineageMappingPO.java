package net.mortalsilence.indiepim.server.domain;

import javax.persistence.*;

@Entity
@Table(name="msg_tag_lineage_mapping")
public class MessageTagLineageMappingPO {

	@Id
    @GeneratedValue
	@Column(name = "id")
	private Long id;
	
	@Column(name = "msg_uid")
	private Long msgUid; 

	@ManyToOne(optional=false) // no cascade!
	@JoinColumn(name="message_id", referencedColumnName="id", nullable=false)
	private MessagePO message;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="tag_lineage_id", referencedColumnName="id", nullable=false)
	private TagLineagePO tagLineage;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMsgUid() {
		return msgUid;
	}

	public void setMsgUid(Long msgUid) {
		this.msgUid = msgUid;
	}

	public MessagePO getMessage() {
		return message;
	}

	public void setMessage(MessagePO message) {
		this.message = message;
	}

	public TagLineagePO getTagLineage() {
		return tagLineage;
	}

	public void setTagLineage(TagLineagePO tagLineage) {
		this.tagLineage = tagLineage;
	}
	
	
}
