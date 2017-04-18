package net.mortalsilence.indiepim.server.message;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public interface MessageConstants {

    enum EMAIL_ADDRESS_TYPE {
        HOME,
        WORK,
        OTHER
    }

    enum MESSAGE_DELETE_MODE {
        MOVE_2_TRASH,
        MARK_DELETED,
        EXPUNGE
    }

	enum EncryptionMode {
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
	
	enum AuthenticationMode {
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
	
	enum SyncUpdateMethod {
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
	
	String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
    String CONTENT_TYPE_TEXT_PLAIN_UTF8 = CONTENT_TYPE_TEXT_PLAIN + "; charset=utf-8";
	String CONTENT_TYPE_TEXT_HTML = "text/html";
    String CONTENT_TYPE_TEXT_HTML_UTF8 = CONTENT_TYPE_TEXT_HTML + "; charset=utf-8";
    String CONTENT_TYPE_TEXT_ALL = "text/*";
    String CONTENT_TYPE_IMAGE_ALL = "image/*";
    String CONTENT_TYPE_MULTIPART_ALL = "multipart/*";
	String CONTENT_TYPE = "Content-Type:";
    String CONTENT_TYPE_PARAM_CHARSET = "charset";

	String PROTOCOL_IMAP = "IMAP";
	String PROTOCOL_POP3 = "POP3";
	
	String DATETIME_FORMAT_EUR = "yyyy-MM-dd HH.mm.ss";

    String DEFAULT_FOLDER_SENT = "Sent";
    String DEFAULT_FOLDER_TRASH = "Trash";
    String DEFAULT_FOLDER_JUNK = "Junk";
    String DEFAULT_FOLDER_DRAFTS = "Drafts";
    String DEFAULT_FOLDER_INBOX = "INBOX";

    String DISPOSITION_ATTACHMENT = "attachment";
    String DISPOSITION_INLINE = "inline";
}
