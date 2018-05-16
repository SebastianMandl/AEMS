package at.aems.apilib.crypto;

import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

//This classed was copied due to portability reasons!
/**
 * This class provides decryption functionality. It decrypts using the AES
 * algorithm.
 * 
 * This class will be used at client side as well as at server side and database
 * side.
 * 
 * @author Sebastian
 * @since 25-07-2017
 * @version 1.0
 */
public class Decrypter {

    /**
     * This function is utilized to decrypt prior encrypted bytes with AES.
     * 
     * @param key
     *            - the key formatted according to AES
     * @param encrypted
     *            - the encrypted bytes which shell be decrypted
     * @return the decrypted byte array version of the encrypted byte array passed
     *         into this function.
     * @throws IllegalBlockSizeException
     *             - if the block size is not a multiple of the key length
     * @throws BadPaddingException
     *             - if the padding of the encrypted bytes is invalid
     * @throws InvalidKeyException
     *             - if the key is not valid for the specified algoritm AES
     * @throws NoSuchAlgorithmException
     *             - this exception cannot occur since this function takes care of
     *             the integrity of the decryption functionality.
     * @throws NoSuchPaddingException
     *             - if the padding is not proper
     */
    public static byte[] requestDecryption(byte[] key, byte[] encrypted) throws IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        Key keyBytes = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keyBytes);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static byte[] requestDecryption(BigDecimal key, byte[] encrypted) throws IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        return requestDecryption(key.toString().getBytes(), encrypted);
    }
}
