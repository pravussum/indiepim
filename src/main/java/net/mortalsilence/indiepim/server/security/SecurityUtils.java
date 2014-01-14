package net.mortalsilence.indiepim.server.security;

import net.mortalsilence.indiepim.server.domain.MessageAccountPO;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Named
public class SecurityUtils {

    @Inject
    private EncryptionService encryptionService;

	public String getAccountPassword(MessageAccountPO account) {
		return encryptionService.decypher(account.getPassword());
	}

    public String hashUserPassword(final String userName, final String password) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            final String str = password + "{" + userName + "}";
            md.update(str.getBytes("UTF-8"));
            byte[] hash = md.digest();
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
