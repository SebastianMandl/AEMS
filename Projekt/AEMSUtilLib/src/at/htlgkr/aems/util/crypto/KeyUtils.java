package at.htlgkr.aems.util.crypto;

import java.math.BigDecimal;

import at.htlgkr.aems.util.hashcode.HashCode;

public class KeyUtils {

	public static BigDecimal salt(BigDecimal key, String username, String password) {
		key = key.multiply(new BigDecimal(String.valueOf(HashCode.getHashCode(username))));
		key = key.multiply(new BigDecimal(String.valueOf(HashCode.getHashCode(password))));
		return key;
	}
	
}
