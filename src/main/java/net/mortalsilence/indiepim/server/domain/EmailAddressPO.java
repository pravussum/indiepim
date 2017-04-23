package net.mortalsilence.indiepim.server.domain;

import net.mortalsilence.indiepim.server.message.MessageConstants;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "email_address")
@SuppressWarnings("serial")
public class EmailAddressPO implements Serializable, PersistentObject, MessageConstants {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(optional=false)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private UserPO user;
	
	@Column(name = "email_address")
	private String emailAddress;
	
	@ManyToOne(optional=true)
	@JoinColumn(name="contact_id", referencedColumnName="id")
	private ContactPO contact;

	@ManyToMany(mappedBy="emailAddresses")
	private List<MessagePO> messages;

    @Column(name = "type")
    private String type;

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

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public ContactPO getContact() {
		return contact;
	}

	public void setContact(ContactPO contact) {
		this.contact = contact;
        if(!contact.getEmailAddresses().contains(this))
            contact.getEmailAddresses().add(this);
	}

	public void setMessages(List<MessagePO> messages) {
		this.messages = messages;
	}

    public EMAIL_ADDRESS_TYPE getType() {
        return EMAIL_ADDRESS_TYPE.valueOf(type);
    }

    public void setType(EMAIL_ADDRESS_TYPE type) {
        this.type = type.name();
    }

    public Boolean getPrimary() {
        return isPrimary;
    }

    public void setPrimary(Boolean primary) {
        isPrimary = primary;
    }
}
