package net.mortalsilence.indiepim.server.domain;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "tag_hierarchy")
@SuppressWarnings("serial")
public class TagHierarchyPO implements Serializable, PersistentObject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private UserPO user;
	
	@Column(name="name")
	private String name;

	@OneToMany(mappedBy="tagHierarchy", targetEntity=TagLineagePO.class)
	@Cascade( {CascadeType.DELETE})
	private List<TagLineagePO> tagLineages;
	
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TagLineagePO> getTagLineages() {
		return tagLineages;
	}

	public void setTagLineages(List<TagLineagePO> tagLineages) {
		this.tagLineages = tagLineages;
	}
	
}
