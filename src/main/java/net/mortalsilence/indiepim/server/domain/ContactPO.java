package net.mortalsilence.indiepim.server.domain;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = "contact")
@SuppressWarnings("serial")
public class ContactPO implements Serializable, PersistentObject {

	@Id
    @GeneratedValue
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private UserPO user;
	
	@OneToMany(mappedBy="contact", targetEntity=EmailAddressPO.class, cascade = CascadeType.ALL)
	private List<EmailAddressPO> emailAddresses = new LinkedList<EmailAddressPO>();
	
	@Column(name = "display_name")
	private String displayName;

    @Column(name = "family_name")
    private String familyName;

    @Column(name = "given_name")
    private String givenName;

    @Column(name = "origin")
    private String origin;

	@ManyToMany
	@JoinTable(name="contact_tag_mapping",
			joinColumns =
			@JoinColumn(name="contact_id", referencedColumnName="id"),
			inverseJoinColumns = 
			@JoinColumn(name="tag_id", referencedColumnName="id")
		)
	private List<TagPO> tags = new LinkedList<TagPO>();
	
	@OneToMany(mappedBy="contact", targetEntity=PhoneNoPO.class)
	private List<PhoneNoPO> phoneNos = new LinkedList<PhoneNoPO>();

	@OneToMany(mappedBy="contact", targetEntity=AddressPO.class)
	private List<AddressPO> addresses = new LinkedList<AddressPO>();

	@OneToMany(mappedBy="contact", targetEntity=DatePO.class)
	private List<DatePO> dates = new LinkedList<DatePO>();

	@OneToMany(mappedBy="contact", targetEntity=UrlPO.class)
	private List<UrlPO> urls = new LinkedList<UrlPO>();

    @OneToOne(targetEntity = ImagePO.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "contact_image_id")
    private ImagePO photo;

    @ManyToMany(fetch= FetchType.LAZY)
   	@Cascade({org.hibernate.annotations.CascadeType.PERSIST, org.hibernate.annotations.CascadeType.SAVE_UPDATE})
   	@JoinTable(name="contact_tag_lineage_mapping",
   			joinColumns =
   			@JoinColumn(name="tag_lineage_id", referencedColumnName="id"),
   			inverseJoinColumns =
   			@JoinColumn(name="contact_id", referencedColumnName="id")
   		)
   	private List<TagLineagePO> tagLineages = new LinkedList<TagLineagePO>();

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

	public List<EmailAddressPO> getEmailAddresses() {
		return emailAddresses;
	}

	public void setEmailAddresses(List<EmailAddressPO> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

    public void addEmailAddress(EmailAddressPO emailAddress) {
        this.emailAddresses.add(emailAddress);
        if(emailAddress.getContact() != this)
            emailAddress.setContact(this);
    }

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String name) {
		this.displayName = name;
	}

	public List<TagPO> getTags() {
		return tags;
	}

	public void setTags(List<TagPO> tags) {
		this.tags = tags;
	}

	public List<PhoneNoPO> getPhoneNos() {
		return phoneNos;
	}

	public void setPhoneNos(List<PhoneNoPO> phoneNos) {
		this.phoneNos = phoneNos;
	}

    public void addPhoneNo(PhoneNoPO phoneNo) {
        this.phoneNos.add(phoneNo);
        if(phoneNo.getContact() != this)
            phoneNo.setContact(this);
    }

	public List<AddressPO> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<AddressPO> addresses) {
		this.addresses = addresses;
	}

    public void addAddress(AddressPO address) {
        this.addresses.add(address);
        if(address.getContact() != this)
            address.setContact(this);
    }

	public List<DatePO> getDates() {
		return dates;
	}

	public void setDates(List<DatePO> dates) {
		this.dates = dates;
	}

	public List<UrlPO> getUrls() {
		return urls;
	}

	public void setUrls(List<UrlPO> urls) {
		this.urls = urls;
	}

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public ImagePO getPhoto() {
        return photo;
    }

    public void setPhoto(ImagePO photo) {
        this.photo = photo;
    }

    public List<TagLineagePO> getTagLineages() {
        return tagLineages;
    }

    public void setTagLineages(List<TagLineagePO> tagLineages) {
        this.tagLineages = tagLineages;
    }

    public Set <TagPO> getAllTags(){
		Set<TagPO> result = new TreeSet<TagPO>();
		result.addAll(getTags());
		for(TagLineagePO tagLineage : getTagLineages()) {
			result.addAll(tagLineage.getTags());
		}
		return result;
	}
}
