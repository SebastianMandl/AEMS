package at.htlgkr.aems.util.crypto;

import java.math.BigDecimal;

import at.htlgkr.aems.util.hashcode.HashCode;
import at.htlgkr.aems.util.key.DiffieHellmanProcedure;

public class KeyUtils {

	public static BigDecimal salt(BigDecimal key, String username, String password) {
		key = key.multiply(new BigDecimal(String.valueOf(HashCode.getHashCode(username))));
		key = key.multiply(new BigDecimal(String.valueOf(HashCode.getHashCode(password))));
		return new BigDecimal(key.abs().toString().substring(0, DiffieHellmanProcedure.KEY_LENGTH));
	}
	
}
