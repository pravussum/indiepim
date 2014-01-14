package net.mortalsilence.indiepim.server.domain;

import net.mortalsilence.indiepim.server.contact.ContactConstants;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "phone_no")
@SuppressWarnings("serial")
public class PhoneNoPO implements Serializable, PersistentObject, ContactConstants {
	
	@Id
    @GeneratedValue
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private UserPO user;
	
	@Column(name = "type")
	private String type;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="contact_id", referencedColumnName="id")
	private ContactPO contact;
	
	@Column(name = "phone_no")
	private String phoneNo;

    @Column(name = "is_primary")
    private Boolean isPrimary;

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

	public PHONE_NUMBER_TYPE getType() {
		return PHONE_NUMBER_TYPE.valueOf(type);
	}

	public void setType(PHONE_NUMBER_TYPE type) {
		this.type = type.name();
	}

	public ContactPO getContact() {
		return contact;
	}

	public void setContact(ContactPO contact) {
		this.contact = contact;
        if(!contact.getPhoneNos().contains(this))
            contact.getPhoneNos().add(this);
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

    public Boolean getPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }
}
