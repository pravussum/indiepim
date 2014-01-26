package net.mortalsilence.indiepim.server.message;

import net.mortalsilence.indiepim.server.SharedConstants;
import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.dao.TagDAO;
import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import net.mortalsilence.indiepim.server.domain.TagHierarchyPO;
import net.mortalsilence.indiepim.server.domain.TagLineagePO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import net.mortalsilence.indiepim.server.exception.NotImplementedException;
import net.mortalsilence.indiepim.server.security.SecurityUtils;
import net.mortalsilence.indiepim.server.utils.ArgUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.*;
import java.util.*;

/*

	Protocol	Store or	Uses	Supports
	Name		Transport?	SSL?	STARTTLS?
	-------------------------------------------------
	imap		Store		No	Yes
	imaps		Store		Yes	N/A
	gimap		Store		Yes	N/A
	pop3		Store		No	Yes
	pop3s		Store		Yes	N/A
	smtp		Transport	No	Yes
	smtps		Transport	Yes	N/A

 */


@Named
public class ConnectionUtils implements MessageConstants {

    @Inject private SecurityUtils securityUtils;
    @Inject private TagDAO tagDAO;
    @Inject private GenericDAO genericDAO;

	private boolean debug = false;
	
	public Store connectToStore(final MessageAccountPO account, final Session session) {
		final String password = securityUtils.getAccountPassword(account);
		final Integer port = account.getPort();

		final Store store;
		try {
			store = session.getStore(getIncomingProtocol(account));
            final String host = account.getHost();
            store.connect(host, port, account.getUsername(), password);
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			throw new RuntimeException("Mail server login failed: " + e.getMessage());
		} catch (MessagingException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Mail server login failed: " + e1.getMessage());
		}
		return store;
	}

	public Transport getTransport(final MessageAccountPO account, final Session session, final String protocol) {
		
		final String password = securityUtils.getAccountPassword(account);
		final Integer port = account.getOutgoingPort();
		final Transport transport;
		try {
			transport = session.getTransport(protocol);
			transport.connect(account.getOutgoingHost(), port, account.getEmail(),password); 
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			throw new RuntimeException("Mail server login failed: " + e.getMessage());
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException("Mail server login failed: " + e.getMessage());
		}
		
		return transport;
	}
	
	public Session getSession(final MessageAccountPO account, final boolean incoming) {
		// Get system properties
		final Properties props = System.getProperties();
		/* Protocol */
		props.setProperty("mail.store.protocol", getOutgoingProtocol(account));
        final String protocol = incoming ? getIncomingProtocol(account) : getOutgoingProtocol(account);

		props.remove("mail." + protocol + ".host");
		props.remove("mail." + protocol + ".port");
		props.remove("mail." + protocol + ".user");
		props.remove("mail.user");		

		/* Authentication */
		final AuthenticationMode authenticationMode = AuthenticationMode.valueOf(incoming ? account.getAuthentication() : account.getOutgoingAuthentication());
		final String auth = authenticationMode == AuthenticationMode.PASSWORD_NORMAL ? "true" : "false";
        props.put("mail." + protocol + ".auth", auth);

		/* Encryption */
		final EncryptionMode encryptionMode = EncryptionMode.valueOf(incoming ? account.getEncryption() : account.getOutgoingEncryption());
            final String starttls = encryptionMode == EncryptionMode.STARTTLS ? "true" : "false";
            props.put("mail." + protocol + ".starttls.enable", starttls);

		props.put("mail.debug", debug? "true" : "false");
		if(account.getTrustInvalidSSLCertificates()) {
            /*
            SELF SIGNED CERTIFICATES

            JavaMail now includes a special SSL socket factory that can simplify
            dealing with servers with self-signed certificates.  While the
            recommended approach is to include the certificate in your keystore
            as described above, the following approach may be simpler in some cases.

            The class com.sun.mail.util.MailSSLSocketFactory can be used as a
            simple socket factory that allows trusting all hosts or a specific set
            of hosts.  For example:

            ...

            Use of MailSSLSocketFactory avoids the need to add the certificate to
            your keystore as described above, or configure your own TrustManager
            as described below.
            ---
            mail.smtp.ssl.trust
            If set, and a socket factory hasn't been specified, enables use of a
            MailSSLSocketFactory. If set to "*", all hosts are trusted. If set to a
            whitespace separated list of hosts, those hosts are trusted. Otherwise, trust
            depends on the certificate the server presents.

            */
            props.put("mail." + protocol + ".ssl.trust", incoming ? account.getHost() : account.getOutgoingHost());

            /*  -- Server Identity Check --
                RFC 2595 specifies addition checks that must be performed on the
                server's certificate to ensure that the server you connected to is
                the server you intended to connect to.  This reduces the risk of
                "man in the middle" attacks.  For compatibility with earlier releases
                of JavaMail, these additional checks are disabled by default.  We
                    strongly recommend that you enable these checks when using SSL.  To
                    enable these checks, set the "mail..ssl.checkserveridentity"
                    property to "true".
            */
            props.put("mail." + protocol + ".ssl.checkserveridentity", "false"); // accept self-signed certificate ?


            // TODO [Security] ask user during account setup and import certificate to JDK trust store
            // keytool -import -file your_cert.cer /path/to/cacerts, the password is changeit
        }

		Session session = Session.getInstance(props);
		return session;
	}

	public String getIncomingProtocol(final MessageAccountPO account) {
		final EncryptionMode encryptionMode = EncryptionMode.valueOf(account.getEncryption());
		if(ArgUtils.empty(account.getProtocol()))
			throw new RuntimeException("Protocol for account " + account.getName() + " is not set.");
		if(PROTOCOL_IMAP.equals(account.getProtocol().toUpperCase())) {			
			return encryptionMode == EncryptionMode.SSL ? "imaps" : "imap";
		} else if(PROTOCOL_POP3.equals(account.getProtocol().toUpperCase())) {
			return encryptionMode == EncryptionMode.SSL ? "imaps" : "imap";
		}
		throw new NotImplementedException("Protocol " + account.getProtocol() + " not (yet) supported. Valid values are " + PROTOCOL_IMAP + " and " + PROTOCOL_POP3);
	}
	
	public String getOutgoingProtocol(final MessageAccountPO account) {
		final EncryptionMode encryptionMode = EncryptionMode.valueOf(account.getEncryption());
		return encryptionMode == EncryptionMode.SSL ? "smtps" : "smtp";
	}

    public String getFolderPathFromTagLineage(char separator, TagLineagePO tagLineage) {
        final String[] parts = StringUtils.split(tagLineage.getLineage(), SharedConstants.TAG_LINEAGE_SEPARATOR.toString());
        return StringUtils.join(parts, separator);
    }

    public String getSentFolderPath(MessageAccountPO account, Store store) throws MessagingException {
        final char separator = store.getDefaultFolder().getSeparator();
        if(account.getSentFolder() == null)
            return getDefaultFolderName(store) + separator + DEFAULT_FOLDER_SENT;
        return getFolderPathFromTagLineage(separator, account.getSentFolder());
    }

    public String getTrashFolderPath(MessageAccountPO account, Store store) throws MessagingException {
        final char separator = store.getDefaultFolder().getSeparator();
        if(account.getTrashFolder() == null)
            return getDefaultFolderName(store) + separator + DEFAULT_FOLDER_TRASH;
        return getFolderPathFromTagLineage(separator, account.getTrashFolder());
    }

    public String getJunkFolderPath(MessageAccountPO account, Store store) throws MessagingException {
        final char separator = store.getDefaultFolder().getSeparator();
        if(account.getJunkFolder() == null)
            return getDefaultFolderName(store) + separator + DEFAULT_FOLDER_JUNK;
        return getFolderPathFromTagLineage(separator, account.getJunkFolder());
    }

    public String getDraftsFolderPath(MessageAccountPO account, Store store) throws MessagingException {
        final char separator = store.getDefaultFolder().getSeparator();
        if(account.getDraftsFolder() == null)
            return getDefaultFolderName(store) + separator + DEFAULT_FOLDER_DRAFTS;
        return getFolderPathFromTagLineage(separator, account.getDraftsFolder());
    }

    private String getDefaultFolderName(Store store) throws MessagingException {
//         return store.getDefaultFolder().getName();
        // TODO store.getDefaultFolder() seems to return empty strings, at least sometimes... what to do about it?
        return DEFAULT_FOLDER_INBOX;
    }


    public TagLineagePO getOrCreateTagLineage(final UserPO user, final MessageAccountPO account, final Folder folder) {
        final List<String> tags = new LinkedList<String>();
        try {
            StringTokenizer st = new StringTokenizer(folder.getFullName(), new Character(folder.getSeparator()).toString());
            while(st.hasMoreElements()) {
                tags.add(st.nextToken());
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing folder name " + folder.getFullName(), e);
        }

        if(account.getTagHierarchy() == null) {
            createTagHierarchy4Account(user, account);
        }

        final List<String> partialTagLineageTags = new LinkedList<String>();
        final Iterator<String> tagIt = tags.iterator();
        TagLineagePO tagLineage = null;
        while(tagIt.hasNext()) {
            partialTagLineageTags.add(tagIt.next());
            final String tagLineageStr = StringUtils.join(partialTagLineageTags, SharedConstants.TAG_LINEAGE_SEPARATOR);
            tagLineage = tagDAO.getOrCreateTagLineage(user, tagLineageStr, account.getTagHierarchy());
        }

        return tagLineage;
    }

    public void createTagHierarchy4Account(final UserPO user, final MessageAccountPO account) {
        TagHierarchyPO tagHierarchy = new TagHierarchyPO();
        tagHierarchy.setUser(user);
        tagHierarchy.setName(account.getTag().getTag());
        genericDAO.persist(tagHierarchy);
        account.setTagHierarchy(tagHierarchy);
    }
}
