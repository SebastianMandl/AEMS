package at.htlgkr.aems.util.crypto;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Decrypter {

	public static String requestDecryption(BigDecimal key, int keyLength, byte[] encrypted) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		Key keyBytes = new SecretKeySpec(key.toString().substring(0, keyLength).getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, keyBytes);
		byte[] decrypted = cipher.doFinal(encrypted);
		return new String(decrypted);
	}
	
}
