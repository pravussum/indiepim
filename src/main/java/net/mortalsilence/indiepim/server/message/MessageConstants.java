package net.mortalsilence.indiepim.server.message;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public interface MessageConstants {

    public enum EMAIL_ADDRESS_TYPE {
        HOME,
        WORK,
        OTHER
    }

    public enum MESSAGE_DELETE_MODE {
        MOVE_2_TRASH,
        MARK_DELETED,
        EXPUNGE
    }

	public enum EncryptionMode {
		NONE (1), 
		STARTTLS (2),
		TLS (3), 
		SSL (4);
        
        private Integer value;
        private static final Map<Integer,EncryptionMode> lookup = new HashMap<Integer,EncryptionMode>();
        static {
            for(EncryptionMode s : EnumSet.allOf(EncryptionMode.class))
                lookup.put(s.getValue(), s);
        }
        EncryptionMode(Integer value) { this.value = value;}
        public Integer getValue() { return value;}
        public static EncryptionMode get(int value) { return lookup.get(value); }
    }
	
	public enum AuthenticationMode {
		NONE (1),
		PASSWORD_NORMAL (2);
        
        private Integer value;
        private static final Map<Integer,AuthenticationMode> lookup = new HashMap<Integer,AuthenticationMode>();
        static {
            for(AuthenticationMode s : EnumSet.allOf(AuthenticationMode.class))
                lookup.put(s.getValue(), s);
        }
        AuthenticationMode(Integer value) { this.value = value;}
        public Integer getValue() { return value;}
        public static AuthenticationMode get(int value) { return lookup.get(value); }        
	}
	
	public enum SyncUpdateMethod {
		NONE (0),
        FLAGS (1),
		FULL (2);
		private Integer value;
		private static final Map<Integer,SyncUpdateMethod> lookup = new HashMap<Integer,SyncUpdateMethod>();
	    static {	
	    	for(SyncUpdateMethod s : EnumSet.allOf(SyncUpdateMethod.class))
	    		lookup.put(s.getValue(), s);  	
	    }
		SyncUpdateMethod(Integer value) { this.value = value;}
		public Integer getValue() { return value;}
	    public static SyncUpdateMethod get(int value) { return lookup.get(value); }
	}
	
	public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
    public static final String CONTENT_TYPE_TEXT_PLAIN_UTF8 = CONTENT_TYPE_TEXT_PLAIN + "; charset=utf-8";
	public static final String CONTENT_TYPE_TEXT_HTML = "text/html";
    public static final String CONTENT_TYPE_TEXT_HTML_UTF8 = CONTENT_TYPE_TEXT_HTML + "; charset=utf-8";
    public static final String CONTENT_TYPE_TEXT_ALL = "text/*";
    public static final String CONTENT_TYPE_IMAGE_ALL = "image/*";
    public static final String CONTENT_TYPE_MULTIPART_ALL = "multipart/*";
	public static final String CONTENT_TYPE = "Content-Type:";
    public static final String CONTENT_TYPE_PARAM_CHARSET = "charset";

	public static final String PROTOCOL_IMAP = "IMAP";
	public static final String PROTOCOL_POP3 = "POP3";
	
	public static final String DATETIME_FORMAT_EUR = "yyyy-MM-dd HH.mm.ss";

    public static final String DEFAULT_FOLDER_SENT = "Sent";
    public static final String DEFAULT_FOLDER_TRASH = "Trash";
    public static final String DEFAULT_FOLDER_JUNK = "Junk";
    public static final String DEFAULT_FOLDER_DRAFTS = "Drafts";
    public static final String DEFAULT_FOLDER_INBOX = "INBOX";
}
