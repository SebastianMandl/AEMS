package at.htlgkr.aems.util.arrays;

public class ArrayUtil {

	public static byte[] convertIntoPrimitiveArray(Byte[] bytes) {
		byte[] newBytes = new byte[bytes.length];
		for(int i = 0; i < bytes.length; i++) {
			newBytes[i] = bytes[i];
		}
		return newBytes;
	}
	
}
