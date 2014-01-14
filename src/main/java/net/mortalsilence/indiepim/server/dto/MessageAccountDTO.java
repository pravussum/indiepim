package net.mortalsilence.indiepim.server.dto;

import net.mortalsilence.indiepim.server.tags.TagHierarchyTree;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class MessageAccountDTO implements Serializable {

	public Long id;
	public String email;
	public String accountName;
	public String userName;
	public String host;
	public String outgoingHost;
	public Integer port;
	public Integer outgoingPort;
	public String authentication;
	public String outgoingAuthentication;
	public String encryption;
	public String outgoingEncryption;
	public String tag;
	public String password;
	public String protocol;
	public TagHierarchyTree tagHierarchy;
	public Integer syncMethod;
	public Integer syncInterval;
	public Date lastSyncRun;
	public Boolean newMessages;
	public Boolean trustInvalidSSLCertificates;
	public Timestamp version;
}
