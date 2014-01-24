package net.mortalsilence.indiepim.server.utils;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;
import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import net.mortalsilence.indiepim.server.domain.MessagePO;
import net.mortalsilence.indiepim.server.domain.TagPO;
import net.mortalsilence.indiepim.server.dto.MessageAccountDTO;
import net.mortalsilence.indiepim.server.dto.MessageDTO;
import net.mortalsilence.indiepim.server.dto.MessageListDTO;
import net.mortalsilence.indiepim.server.dto.TagDTO;
import net.mortalsilence.indiepim.server.exception.NotImplementedException;
import net.mortalsilence.indiepim.server.message.MessageConstants;
import net.mortalsilence.indiepim.server.security.EncryptionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.mozilla.universalchardet.UniversalDetector;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Named
public class MessageUtils implements MessageConstants {

    @Inject
    private EncryptionService encryptionService;

	final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");
	
	public List<MessageListDTO> mapMessagePOtoMessageDTOList(List<MessagePO> messages) {
		final List<MessageListDTO> result = new ArrayList<MessageListDTO>();
		if(messages == null)
			return result; 
		for(MessagePO msg : messages) {
			MessageListDTO resultMsg = new MessageListDTO();
			resultMsg.subject = msg.getSubject() != null ? msg.getSubject() : "<No Subject>";
			resultMsg.msgId = msg.getId();
			resultMsg.dateReceived = msg.getDateReceived();
			resultMsg.read = msg.getRead();
            resultMsg.deleted = msg.getDeleted();
            resultMsg.draft = msg.getDraft();
			if(msg.getAttachments() != null && !msg.getAttachments().isEmpty())
				resultMsg.hasAttachment = true;
			else 
				resultMsg.hasAttachment = false;
			resultMsg.sender = getRealNames(msg.getSender());
            final Set<TagPO> allTags = msg.getAllTags();
            for(TagPO tag : allTags) {
				TagDTO tagDto = new TagDTO();
				tagDto.tag = tag.getTag();
				tagDto.color = tag.getColor();
				resultMsg.tags.add(tagDto);
			}
			// TODO handle HTML only mails
            if(msg.getContentText() != null)
				resultMsg.contentPreview = net.mortalsilence.indiepim.server.utils.StringUtils.extractContentPreview(msg.getContentText());
			result.add(resultMsg);
		}
		return result;
	}

    public MessageDTO mapMessagePOtoMessageDTO(MessagePO message) {
		if(message == null)
			return null; 
		MessageDTO result = new MessageDTO();
		result.id = message.getId();
		result.accountId = message.getMessageAccount().getId();
		result.subject = message.getSubject();
		result.tags = new HashSet<TagDTO>();
		for(TagPO tag : message.getAllTags()) {
			TagDTO tagDto = new TagDTO();
			tagDto.tag = tag.getTag();
			tagDto.color = tag.getColor();
			result.tags.add(tagDto);
		}
		result.sender = message.getSender();
        result.senderEmail = message.getEmailAddresses().isEmpty() ? null : message.getEmailAddresses().get(0).getEmailAddress();
		result.receiver = message.getReceiver() != null ? Arrays.asList(StringUtils.split(message.getReceiver(), ",")) : null;
		result.dateReceived = message.getDateReceived();
		result.read = message.getRead();
        result.deleted = message.getDeleted();
        result.draft = message.getDraft();
		if(message.getContentHtml() != null)
			result.contentHtml = message.getContentHtml();
		else result.contentText = message.getContentText();
		if(message.getAttachments() != null && !message.getAttachments().isEmpty())
			result.hasAttachment = true;
		else 
			result.hasAttachment = false;
		return result;
	}	

	public MessageAccountDTO mapMessageAccountPO2MessageAccountDTO(MessageAccountPO messageAccountPO) {
		MessageAccountDTO resultAccount = new MessageAccountDTO();
		resultAccount.accountName = messageAccountPO.getName();
		resultAccount.id = messageAccountPO.getId();
		resultAccount.host = messageAccountPO.getHost();
		resultAccount.email = messageAccountPO.getEmail();
		resultAccount.userName = messageAccountPO.getUsername();
		resultAccount.protocol = messageAccountPO.getProtocol();
		resultAccount.port = messageAccountPO.getPort();
		resultAccount.outgoingPort = messageAccountPO.getOutgoingPort();
		resultAccount.authentication = messageAccountPO.getAuthentication();
		resultAccount.outgoingAuthentication = messageAccountPO.getOutgoingAuthentication();
		resultAccount.encryption = messageAccountPO.getEncryption();
		resultAccount.outgoingEncryption = messageAccountPO.getOutgoingEncryption();
		resultAccount.syncMethod = messageAccountPO.getSyncMethod();
		resultAccount.syncInterval = messageAccountPO.getSyncInterval();
		resultAccount.lastSyncRun = messageAccountPO.getMessageAccountStats().getLastSyncRun();
		resultAccount.newMessages = messageAccountPO.getNewMessages();
		resultAccount.trustInvalidSSLCertificates = messageAccountPO.getTrustInvalidSSLCertificates();			
        // do not give the password away! We are only using it in the backend for message account sync.
        // can be overriden but not read from the clients
		resultAccount.outgoingHost = messageAccountPO.getOutgoingHost();
		resultAccount.tag = messageAccountPO.getTag().getTag();
		if(messageAccountPO.getTagHierarchy() != null)
			resultAccount.tagHierarchy = TagUtils.getTagHierarchyTree(messageAccountPO.getTagHierarchy());
		resultAccount.version = messageAccountPO.getTsUpdate();
		return resultAccount;
	}

	public void mapMessageAccountDTO2MessageAccountPO(final MessageAccountDTO account, MessageAccountPO accountPO) {
		accountPO.setName(account.accountName);
		accountPO.setEmail(account.email);
		accountPO.setHost(account.host);
		accountPO.setUsername(account.userName);
		// only override the password if it is not empty (= when a new password is given)
        if(account.password != null && !"".equals(account.password)) {
            accountPO.setPassword(encryptionService.cypherText(account.password));
        }
		accountPO.setOutgoingHost(account.outgoingHost);
		accountPO.setProtocol(account.protocol);
		accountPO.setPort(account.port);
		accountPO.setOutgoingPort(account.outgoingPort);
		accountPO.setAuthentication(account.authentication);
		accountPO.setOutgoingAuthentication(account.outgoingAuthentication);
		accountPO.setEncryption(account.encryption);
		accountPO.setOutgoingEncryption(account.outgoingEncryption);
		accountPO.setSyncMethod(account.syncMethod);
		accountPO.setSyncInterval(account.syncInterval);
		/* last sync run timestamp is only updated on server side */
		accountPO.setTrustInvalidSSLCertificates(account.trustInvalidSSLCertificates);
		accountPO.setTsUpdate(account.version);
		/* new messages flag is only updated on server side */
	}	

	public String getMsgUID(final Folder folder, final Message message) {
		if(folder == null || message == null)
			throw new IllegalArgumentException("Folder and message must not be null.");
		if(folder instanceof IMAPFolder){
			Long uid;
			try {
				uid = ((IMAPFolder)folder).getUID(message);
				return uid == null ? null : uid.toString();
			} catch (MessagingException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		if(folder instanceof POP3Folder)
			try {
				return ((POP3Folder)folder).getUID(message);
			} catch (MessagingException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		throw new NotImplementedException("Folder type not supported.");
	}

	public Message getMsgByUID(final Folder folder, final Long uid) {
		if(folder == null || uid == null)
			throw new IllegalArgumentException("Folder and uid must not be null.");
		if(folder instanceof IMAPFolder) {
			try {
				return ((IMAPFolder)folder).getMessageByUID(uid);
			} catch (MessagingException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		throw new NotImplementedException("Folder type not supported.");
	}

	public String getHash(final String sender, final String receiver, final String subject, final Date dateReceived) {
		final String dateStr = DateFormatUtils.format(dateReceived, DATETIME_FORMAT_EUR);  
		final List<String> lst = new LinkedList<String>();
		lst.add(sender);
		lst.add(receiver);
		lst.add(subject);
		lst.add(dateStr);
		final String concat = StringHelper.mySqlConcatWs(lst, ";");
		final String result = StringHelper.md5(concat);
		return result;
	}

	public String getSenderStr(final Message message) throws MessagingException {
		Address[] addresses;
		addresses = message.getFrom();
		String[] senders = new String[addresses.length]; 
		for(int j=0; j<addresses.length; j++) {
			try {
				senders[j] = MimeUtility.decodeText(addresses[j].toString());
			} catch (UnsupportedEncodingException e) {
                senders[j] = ((InternetAddress) addresses[j]).getAddress();
			}
		}
		String senderStr = StringUtils.join(senders, ',');
		return senderStr;
	}

	public String getReceiverStr(final Message message) throws MessagingException {
		Address[] addresses = message.getAllRecipients();
		if(addresses == null) {	/* e.g. for drafts */
			return null;
		}
		String[] recipients = new String[addresses.length]; 
		for(int j=0; j<addresses.length; j++) {
			try {
				recipients[j] = MimeUtility.decodeText(addresses[j].toString());
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		return StringUtils.join(recipients, ',');
	}
	
	public String getRealNames(final String completeAddresses) {
		if(completeAddresses == null)
			return null;
		if(completeAddresses.isEmpty())
			return "";
		String[] addresses = StringUtils.split(completeAddresses, ',');
		for(int i=0; i<addresses.length; i++) {
			if(!addresses[i].contains("<"))
				continue;
			addresses[i] = StringUtils.trim(StringUtils.split(addresses[i],'<')[0]);
		}
		return StringUtils.join(addresses, ",");
	}
	
	public String getAutodetectedEncodingString(byte[] bytes) throws UnsupportedEncodingException {
		final UniversalDetector detector = new UniversalDetector(null);
		detector.handleData(bytes, 0, bytes.length);
		detector.dataEnd();
		final String encoding = detector.getDetectedCharset();
		detector.reset();
		if(encoding == null) {
			return null;
		}

        return new String(bytes, encoding);
	}
}