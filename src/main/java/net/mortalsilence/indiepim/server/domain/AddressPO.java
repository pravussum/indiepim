package net.mortalsilence.indiepim.server.domain;

import net.mortalsilence.indiepim.server.contact.ContactConstants;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "address")
@SuppressWarnings("serial")
public class AddressPO implements Serializable, PersistentObject, ContactConstants {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	
	@Column(name = "address")
	private String address;

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

	public ADDRESS_TYPE getType() {
		return ADDRESS_TYPE.valueOf(type);
	}

	public void setType(ADDRESS_TYPE type) {
		this.type = type.name();
	}

	public ContactPO getContact() {
		return contact;
	}

	public void setContact(ContactPO contact) {
		this.contact = contact;
        if(!contact.getAddresses().contains(this))
            contact.getAddresses().add(this);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

    public Boolean getPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }
}
