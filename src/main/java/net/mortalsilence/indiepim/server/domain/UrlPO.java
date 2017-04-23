package net.mortalsilence.indiepim.server.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "url")
@SuppressWarnings("serial")
public class UrlPO implements Serializable, PersistentObject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private UserPO user;
	
	@Column(name = "type")
	private Integer type;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="contact_id", referencedColumnName="id")
	private ContactPO contact;	
	
	@Column(name = "url")
	private String url;

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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public ContactPO getContact() {
		return contact;
	}

	public void setContact(ContactPO contact) {
		this.contact = contact;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
