package net.mortalsilence.indiepim.server.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user_config")
@SuppressWarnings("serial")
public class UserConfigPO implements Serializable, PersistentObject {

	
	@Id
    @GeneratedValue
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private UserPO user;
	
	public UserPO getUser() {
		return user;
	}

	public void setUser(UserPO user) {
		this.user = user;
	}

	@Column(name = "config_key")
	private String key;
	
	@Column(name = "config_value")
	private String value;

	public Long getId() {
		return id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
