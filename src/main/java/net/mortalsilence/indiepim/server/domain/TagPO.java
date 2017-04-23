package net.mortalsilence.indiepim.server.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "tag")
@SuppressWarnings("serial")
public class TagPO implements Serializable, PersistentObject, Comparable<TagPO> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "tag")
	private String tag;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private UserPO user;
	
	@ManyToMany(mappedBy="tags", fetch= FetchType.LAZY)
	private List<MessagePO> messages;

	@ManyToMany(mappedBy="tags", fetch= FetchType.LAZY)
	private List<MessagePO> contacts;

	@ManyToMany(mappedBy="tags", fetch= FetchType.LAZY)
	private List<MessagePO> tagLineages;
	
	@Column(name="color")
	private String color;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public UserPO getUser() {
		return user;
	}

	public void setUser(UserPO user) {
		this.user = user;
	}

	public List<MessagePO> getMessages() {
		return messages;
	}

	public void setMessages(List<MessagePO> messages) {
		this.messages = messages;
	}

	public List<MessagePO> getContacts() {
		return contacts;
	}

	public void setContacts(List<MessagePO> contacts) {
		this.contacts = contacts;
	}

	public List<MessagePO> getTagLineages() {
		return tagLineages;
	}

	public void setTagLineages(List<MessagePO> tagLineages) {
		this.tagLineages = tagLineages;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public int compareTo(TagPO o) {
		return getTag().compareTo(o.getTag());
	}
	
	
}
