package net.mortalsilence.indiepim.server.domain;

import net.mortalsilence.indiepim.server.comet.CometMessage;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Entity
@Table(name = "user")
@SuppressWarnings("serial")
public class UserPO implements Serializable, PersistentObject {

	@Id
    @GeneratedValue
	@Column(name = "id")
    @Field
	private Long id;
	
	@Column(name = "username")
	private String userName;
	
	/**
	 * Calculation of the overall length (59 or 60)
	 * <ul><li>$2$ or $2a$ identifying the hashing algorithm and format,</li>
     * <li>a two digit value denoting the cost parameter, followed by $</li>
     * <li>a 53 characters long base-64-encoded value (they use the alphabet ., /, 0-9, A-Z, a-z that is different to the standard Base 64 Encoding alphabet) consisting of:
     *  <ul><li>22 characters of salt (effectively only 128 bits)</li>
     *  <li>31 characters of encrypted output (effectively only 184 bits)</li></ul>
     *  </li></ul>
	 */
	@Column(name = "pw_hash")
	private String passwordHash;

    @Column(name = "admin")
    private Boolean admin;

    @ContainedIn
    @OneToMany(mappedBy="user", targetEntity=MessagePO.class)
	private List<MessagePO> messages;
	
	@OneToMany(mappedBy="user", targetEntity=MessageAccountPO.class)
	private List<MessageAccountPO> messageAccounts;

	@OneToMany(mappedBy="user", targetEntity=TagPO.class)
	private List<TagPO> tags;
	
	@OneToMany(mappedBy="user", targetEntity=EmailAddressPO.class)
	private List<EmailAddressPO> emailAddresses;

	@OneToMany(mappedBy="user", targetEntity=ContactPO.class)
	private List<ContactPO> contacts;	

	@OneToMany(mappedBy="user", targetEntity=PhoneNoPO.class)
	private List<PhoneNoPO> phoneNos;		

	@OneToMany(mappedBy="user", targetEntity=AddressPO.class)
	private List<AddressPO> addresses;			

	@OneToMany(mappedBy="user", targetEntity=DatePO.class)
	private List<DatePO> dates;			

	@OneToMany(mappedBy="user", targetEntity=UrlPO.class)
	private List<UrlPO> urls;

//	@OneToMany(mappedBy="user", targetEntity=AttachmentPO.class, fetch=FetchType.LAZY)
//	private List<UrlPO> attachments;	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public List<MessagePO> getMessages() {
		return messages;
	}

	public void setMessages(List<MessagePO> messages) {
		this.messages = messages;
	}

	public List<MessageAccountPO> getMessageAccounts() {
		return messageAccounts;
	}

	public void setMessageAccounts(List<MessageAccountPO> messageAccounts) {
		this.messageAccounts = messageAccounts;
	}

	public List<TagPO> getTags() {
		return tags;
	}

	public void setTags(List<TagPO> tags) {
		this.tags = tags;
	}

	public List<EmailAddressPO> getEmailAddresses() {
		return emailAddresses;
	}

	public void setEmailAddresses(List<EmailAddressPO> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}

	public List<ContactPO> getContacts() {
		return contacts;
	}

	public void setContacts(List<ContactPO> contacts) {
		this.contacts = contacts;
	}

	public List<PhoneNoPO> getPhoneNos() {
		return phoneNos;
	}

	public void setPhoneNos(List<PhoneNoPO> phoneNos) {
		this.phoneNos = phoneNos;
	}

	public List<AddressPO> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<AddressPO> addresses) {
		this.addresses = addresses;
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

    public Boolean isAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    //	public List<UrlPO> getAttachments() {
//		return attachments;
//	}
//
//	public void setAttachments(List<UrlPO> attachments) {
//		this.attachments = attachments;
//	}

}
