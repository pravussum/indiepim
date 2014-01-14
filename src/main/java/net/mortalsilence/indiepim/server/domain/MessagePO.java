package net.mortalsilence.indiepim.server.domain;

import org.apache.log4j.Logger;
import org.apache.solr.analysis.HTMLStripCharFilterFactory;
import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.apache.solr.analysis.StandardTokenizerFactory;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.search.annotations.*;
import org.springframework.stereotype.Component;

import javax.inject.Named;
import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

// TODO attachments und tag-mappings per cascading deletes anbinden

@Entity
@Table(name = "message")
@Indexed
@SuppressWarnings("serial")
@AnalyzerDefs({
  @AnalyzerDef(name = "htmlAnalyzer",
      charFilters = {
              @CharFilterDef(factory = HTMLStripCharFilterFactory.class)
      },
    tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
    filters = {
      @TokenFilterDef(factory = LowerCaseFilterFactory.class)
    })
// ,
//  @AnalyzerDef(name = "de",
//    tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
//    filters = {
//      @TokenFilterDef(factory = LowerCaseFilterFactory.class),
//      @TokenFilterDef(factory = GermanStemFilterFactory.class)
//    })
})
@Named
public class MessagePO implements Serializable, PersistentObject {

	final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");
	
	@Id
    @GeneratedValue
	@Column(name = "id")
    @DocumentId
	private Long id;

	@Column(name = "subject")
	@Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String subject;

	@Column(name = "sender")
	private String sender;
	
	@Column(name = "receiver")
	private String receiver;

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
	@Column(name = "content_text")
	private String contentText;

    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    @Analyzer(definition = "htmlAnalyzer")
    @Column(name = "content_html")
	private String contentHtml;

    // only index by lucene to be used for sorting
    @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
	@Column(name = "date_received")
	private Date dateReceived;
	
	@Column(name = "hash")
	private String hash;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="user_id", referencedColumnName="id")
    @IndexedEmbedded
	private net.mortalsilence.indiepim.server.domain.UserPO user;
	
	@ManyToOne(optional=false)
	@JoinColumn(name="msg_account_id", referencedColumnName="id")
	private MessageAccountPO messageAccount;

	@ManyToMany(fetch= FetchType.LAZY)
	@Cascade ({CascadeType.ALL})
	@JoinTable(name="msg_tag_mapping",
			joinColumns =
			@JoinColumn(name="message_id", referencedColumnName="ID"),
			inverseJoinColumns = 
			@JoinColumn(name="tag_id", referencedColumnName="ID")
		)
	private List<TagPO> tags = new LinkedList<TagPO>();

	@ManyToMany(fetch= FetchType.LAZY, cascade = javax.persistence.CascadeType.ALL)
	@JoinTable(name="email_addr_msg_mapping",
			joinColumns =
			@JoinColumn(name="message_id", referencedColumnName="ID"),
			inverseJoinColumns = 
			@JoinColumn(name="email_address_id", referencedColumnName="ID")
		)
	private List<EmailAddressPO> emailAddresses = new LinkedList<EmailAddressPO>();	
	
	@OneToMany( mappedBy="message", cascade=javax.persistence.CascadeType.ALL, targetEntity=AttachmentPO.class)
	private List<AttachmentPO> attachments = new LinkedList<AttachmentPO>();
	
	@Column(name = "size")
	private Integer size;
	
	@OneToMany(mappedBy="message")
//	@Cascade({CascadeType.PERSIST, CascadeType.SAVE_UPDATE, CascadeType.DELETE})
	@Cascade({CascadeType.ALL})
	private Set<MessageTagLineageMappingPO> msgTagLineageMappings = new HashSet<MessageTagLineageMappingPO>();

  	@Column(name = "read_flag")
  	private Boolean read;

	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getContentText() {
		return contentText;
	}

	public void setContentText(String contentText) {
		this.contentText = contentText;
	}

	public MessageAccountPO getMailboxId() {
		return messageAccount;
	}

	public void setMailboxId(MessageAccountPO mailboxId) {
		this.messageAccount = mailboxId;
	}

	public UserPO getUser() {
		return user;
	}

	public void setUser(UserPO user) {
		this.user = user;
	}

	public MessageAccountPO getMessageAccount() {
		return messageAccount;
	}

	public void setMessageAccount(MessageAccountPO messageAccount) {
		this.messageAccount = messageAccount;
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

	public String getContentHtml() {
		return contentHtml;
	}

	public void setContentHtml(String contentHtml) {
		this.contentHtml = contentHtml;
	}

	public List<AttachmentPO> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<AttachmentPO> attachments) {
		this.attachments = attachments;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Date getDateReceived() {
		return dateReceived;
	}

	public void setDateReceived(Date dateReceived) {
		this.dateReceived = dateReceived;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void addTagLineage(final TagLineagePO tagLineage, final Long messageUid) {
        final MessageTagLineageMappingPO mapping = new MessageTagLineageMappingPO();
		mapping.setMessage(this);
		mapping.setTagLineage(tagLineage);
		mapping.setMsgUid(messageUid);
		this.msgTagLineageMappings.add(mapping);
	}

    public boolean hasTagLineage(final TagLineagePO tagLineage, final Long messageUid) {
        for(MessageTagLineageMappingPO mapping : this.msgTagLineageMappings) {
            if(mapping.getTagLineage().equals(tagLineage) && messageUid.equals(mapping.getMsgUid()))
                return true;
        }
        return false;
    }
	
//	// slow!
//	public List<TagPO> getAllTags_() {
//		Long start = Calendar.getInstance().getTimeInMillis();
//		List<TagPO> allTagsForMessage = getAllTagsForMessage(user.getId(), getId());
//		Long end = Calendar.getInstance().getTimeInMillis();
//		logger.debug("getAllTags() took " + (end - start) + "ms.");
//		return allTagsForMessage;
//	}
//
//    @SuppressWarnings("unchecked")
//    public List<TagPO> getAllTagsForMessage(final Long userId, final Long messageId) {
//        return em.createNativeQuery("select t.* from msg_tag_view v, tag t where v.user_id = ?1 and v.message_id = ?2 and t.id = v.tag_id", TagPO.class)
//                .setParameter(1, userId)
//                .setParameter(2, messageId)
//                .getResultList();
//    }
	
	// TODO performance optimization? is it really slow? compare getAllTags_()!
    public Set <TagPO> getAllTags(){
		Set<TagPO> result = new TreeSet<TagPO>();
		result.addAll(getTags());
		result.add(getMessageAccount().getTag());
		for(TagLineagePO tagLineage : getTagLineages()) {
			result.addAll(tagLineage.getTags());
		}
		return result;
	}

	public Set<TagLineagePO> getTagLineages() {
		final Set<TagLineagePO> result = new TreeSet<TagLineagePO>();
		final Iterator<MessageTagLineageMappingPO> it = getMsgTagLineageMappings().iterator();
		while(it.hasNext()) {
			result.add(it.next().getTagLineage());
		}
		return result;
	}
	
	public Set<MessageTagLineageMappingPO> getMsgTagLineageMappings() {
		return msgTagLineageMappings;
	}

	public void setMsgTagLineageMappings(
			Set<MessageTagLineageMappingPO> msgTagLineageMappings) {
		this.msgTagLineageMappings = msgTagLineageMappings;
	}
	
	
	
}
