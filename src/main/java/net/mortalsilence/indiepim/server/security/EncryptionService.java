package net.mortalsilence.indiepim.server.security;

import net.mortalsilence.indiepim.server.SystemConfigKey;
import net.mortalsilence.indiepim.server.dao.ConfigDAO;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.*;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Properties;

@Named
public class EncryptionService {

    @Inject
    private ConfigDAO configDAO;
    @Inject
    private SecurityUtils securityUtils;
    @Value("${indieKeystorePath}") private String keystoreBasePath;
    @Value("#{systemProperties.indieKeystorePassword}") private String keystorePassword;


	final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");
	
	private static final String SEC_KEY_ALIAS = "INDIE_PIM_SEC_KEY";
	private static final String KEYSTORE_FILENAME = ".indieKeystore";

    @PostConstruct
    private void checkEncryptionSetup() {
        final String absoluteKeystorePath = getAbsoluteKeystorePath();
        if(keystorePassword == null || "".equals(keystorePassword)) {
            throw new IllegalArgumentException("Keystore password must not be empty.");
        }
        if(!new File(absoluteKeystorePath).exists()) {
            // try to create
            if(logger.isInfoEnabled())
                logger.info("Creating new keystore using provided password in " + absoluteKeystorePath);
            createKeystore();
        } else {
            if(logger.isInfoEnabled())
                logger.info("Using existing keystore in " + absoluteKeystorePath);
        }
        // try to get the secret key
        getSecKey();
    }

	public void createKeystore() {
		
		if(keystoreBasePath == null || "".equals(keystoreBasePath))
			throw new IllegalArgumentException("The indieKeystorePath must not be empty.");
		final File keystoreBaseDir = new File(keystoreBasePath);
        if(!keystoreBaseDir.exists()) {
            if(!keystoreBaseDir.mkdirs()) {
                throw new IllegalArgumentException("The keystore directory structure (indieKeystorePath) could not be created. Writetable? (" + keystoreBasePath + ")");
            } else if(logger.isDebugEnabled())
                logger.debug("Created directory structure for " + keystoreBaseDir.getAbsolutePath());
        }
        if(!keystoreBaseDir.isDirectory()) {
			throw new IllegalArgumentException("The indieKeystorePath must represent a valid directory: " + keystoreBasePath);
		}
		if(!keystoreBaseDir.canWrite()) {
			throw new IllegalArgumentException("The indieKeystorePath directory cannot be written: " + keystoreBasePath);
		}
		if(new File(getAbsoluteKeystorePath()).exists()) {
            throw new IllegalArgumentException("The file already exists. Cannot create new keystore: " + keystoreBasePath);
        } else {
			/* generate the secret key... */
			final KeyGenerator kgen;
			try {
				kgen = KeyGenerator.getInstance("AES");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				throw new RuntimeException("AES seems not to be supported");
			}
			kgen.init(128); // TODO enough? Make configurable?
			final SecretKey skey = kgen.generateKey();
			
			try {
				/* create a new keystore */
				final KeyStore ks = KeyStore.getInstance("JCEKS");
				ks.load(null, null);
				
				/* store the new secret key */
				final KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(skey);
				ks.setEntry(SEC_KEY_ALIAS, skEntry, new KeyStore.PasswordProtection(keystorePassword.toCharArray()));
				
				ks.store(new FileOutputStream(getAbsoluteKeystorePath()), keystorePassword.toCharArray());
				/* store the key store in the key store base directory */
                if(logger.isInfoEnabled())
                    logger.info("Keystore saved to " + getAbsoluteKeystorePath() + ".");

			} catch (KeyStoreException e) {
				e.printStackTrace();
				throw new RuntimeException("A keystore with type JCEKS could not be instantiated. Java cryptography Extension (JCE) available?");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				throw new RuntimeException("NoSuchAlgorithmException while creating the new keystore");
			} catch (CertificateException e) {
				e.printStackTrace();
				throw new RuntimeException("CertificateException while creating the new keystore");
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("IOException while creating the new keystore in " + keystoreBaseDir.getAbsolutePath());
			}
		}
	}

	private String getAbsoluteKeystorePath() {
		return new File(keystoreBasePath).getAbsolutePath() + System.getProperty("file.separator") + KEYSTORE_FILENAME;
	}
	
	public Key getSecKey() {
		final String keystorePath = getAbsoluteKeystorePath();
        if(logger.isInfoEnabled())
			logger.info("EncryptionService: Loading Keystore from " + keystorePath);
        FileInputStream fis = null;
        try {
			final KeyStore ks = KeyStore.getInstance("JCEKS");
			char[] password = keystorePassword.toCharArray();
			fis = new FileInputStream(getAbsoluteKeystorePath());
			ks.load(fis, password);
			return ks.getKey(SEC_KEY_ALIAS, password);
		} catch (KeyStoreException kse) {
			kse.printStackTrace();
			throw new RuntimeException("The standard key store (JKS) could not be initialized.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("The key store file'" + keystorePath + "' could not be found.");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("The algorithm to load the key store is not supported.");
		} catch (CertificateException e) {
			e.printStackTrace();
			throw new RuntimeException("A CertificateException occurred while loading the key store.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("The key store file'" + keystorePath +  "' was found, but an I/O error occurred. WRONG PASSWORD? (" + e.getMessage() + ")");
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
			throw new RuntimeException("The key " + SEC_KEY_ALIAS + " could not be restored.");
		} finally {
            if(fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.warn(e); // log and ignore
                }
        }
    }
	
	public String cypherText(final String clearText) {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, getSecKey());
			byte[] encrypted = cipher.doFinal(clearText.getBytes());
			return new String(Hex.encodeHex(encrypted));
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("The algorithm to load the key store is not supported.");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			throw new RuntimeException("A NoSuchPaddingException occurred while encrypting the database password.");
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			throw new RuntimeException("The secret key you provided is invalid");
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			throw new RuntimeException("A IllegalBlockSizeException occurred while encrypting the database password.");
		} catch (BadPaddingException e) {
			e.printStackTrace();
			throw new RuntimeException("A BadPaddingException occurred while encrypting the database password.");
		}
	}
	
	public String decypher(final String encryptedText) {
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, getSecKey());
			byte[] original = cipher.doFinal(Hex.decodeHex(encryptedText.toCharArray()));		 
			return new String(original);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("The algorithm to load the key store is not supported.");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			throw new RuntimeException("A NoSuchPaddingException occurred while decrypting the database password.");
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			throw new RuntimeException("The secret key you provided is invalid");
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			throw new RuntimeException("A IllegalBlockSizeException occurred while decrypting the database password.");
		} catch (BadPaddingException e) {
			e.printStackTrace();
			throw new RuntimeException("Error: BadPaddingException. Most likely your accounts where created with a different private key / keystore.");
		} catch (DecoderException e) {
			e.printStackTrace();
			throw new RuntimeException("A DecoderException occurred while decrypting the database password.");
		}
	}

}
