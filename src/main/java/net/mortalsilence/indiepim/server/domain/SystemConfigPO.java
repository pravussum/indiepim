package net.mortalsilence.indiepim.server.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "system_config")
@SuppressWarnings("serial")
public class SystemConfigPO implements Serializable, PersistentObject {

	
	@Id
    @GeneratedValue
	@Column(name = "id")
	private Long id;
	
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
