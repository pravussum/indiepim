package net.mortalsilence.indiepim.server.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "tag_lineage")
@SqlResultSetMappings( {
	@SqlResultSetMapping(name = "idAndTextTagLineage", columns = {
			@ColumnResult(name = "id"),
			@ColumnResult(name = "lineage") })
})
@SuppressWarnings("serial")
public class TagLineagePO implements Serializable, Comparable<TagLineagePO>, PersistentObject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private UserPO user;
	
	@Column(name="lineage")
	private String lineage;

	@ManyToMany(fetch= FetchType.LAZY)
	@JoinTable(name="tag_lineage_tag_mapping",
			joinColumns =
			@JoinColumn(name="tag_lineage_id", referencedColumnName="id"),
			inverseJoinColumns = 
			@JoinColumn(name="tag_id", referencedColumnName="id")
		)
	private List<TagPO> tags = new LinkedList<TagPO>();	

	@ManyToOne(optional=false)
	@JoinColumn(name="tag_hierarchy_id", referencedColumnName="id")
	private TagHierarchyPO tagHierarchy;	

    @ManyToMany(mappedBy="tagLineages")
   	private List<ContactPO> contacts;

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

	public String getLineage() {
		return lineage;
	}

	public void setLineage(String lineage) {
		this.lineage = lineage;
	}

	public List<TagPO> getTags() {
		return tags;
	}

	public void setTags(List<TagPO> tags) {
		this.tags = tags;
	}

	public TagHierarchyPO getTagHierarchy() {
		return tagHierarchy;
	}

	public void setTagHierarchy(TagHierarchyPO tagHierarchy) {
		this.tagHierarchy = tagHierarchy;
	}

	@Override
	public int compareTo(TagLineagePO o) {
		return this.getLineage().compareTo(o.getLineage());	
	}
}
