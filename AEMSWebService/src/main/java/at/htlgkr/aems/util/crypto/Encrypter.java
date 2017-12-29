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

/**
 * This class provides encryption functionality.
 * It encrypts using the AES Algorithm.
 * 
 * This class will be used at client side as well as at server side and database side.
 * @author Sebastian
 * @since 28-07-2017
 * @version 1.0
 * 
 */
public class Encrypter {

	/**
	 * This function is utilized to encrypt raw bytes with AES.
	 * @param key - the key formatted according to AES
	 * @param raw - the raw bytes which shell be encrypted
	 * @return the encrypted byte array version of the raw byte array passed into this function.
	 * @throws IllegalBlockSizeException - if the block size is not a multiple of the key length
	 * @throws BadPaddingException - if the padding of the encrypted bytes is invalid
	 * @throws InvalidKeyException - if the key is not valid for the specified algoritm AES
	 * @throws NoSuchAlgorithmException - this exception cannot occur since this function takes care of the integrity of the encryption functionality.
	 * @throws NoSuchPaddingException - if the padding is not proper
	 */
	public static byte[] requestEncryption(byte[] key, byte[] raw) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		Key keyBytes = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, keyBytes);
		byte[] encrypted = cipher.doFinal(raw);
		return encrypted;
	}
	
	public static byte[] requestEncryption(BigDecimal key, byte[] raw) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException{
		return requestEncryption(key.toString().getBytes(), raw);
	}
	
}
