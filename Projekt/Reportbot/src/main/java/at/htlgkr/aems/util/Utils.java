package at.htlgkr.aems.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Utils {
  public static String removePortFromUrl(URL url) {
    String s = url.toString();
    int port = url.getPort();
    return s.replaceAll(":" + port, "");
  }
  
  public static void saveStreamAsFile(InputStream input, File outputFile) {
    
    OutputStream output = null;
    
    try {
      output = new FileOutputStream(outputFile);
      
      int read = 0;
      byte[] b = new byte[1024];
      
      while((read = input.read(b)) != -1) {
        output.write(b, 0, read);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if(input != null)
          input.close();
        
        if(output != null)
          output.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
  }
  
public static String decryptAes(String toDecrypt, String aesKey) {
    
    try {
      MessageDigest sha = MessageDigest.getInstance("SHA-1");
      byte[] decryptKey = sha.digest(aesKey.getBytes());
      // Trim length so that the decrypeKey array will be suitable to be turned into an AES key
      decryptKey = Arrays.copyOf(decryptKey, 16);
      
      SecretKeySpec key = new SecretKeySpec(decryptKey, "AES");
      
      Cipher decrypter = Cipher.getInstance("AES");
      decrypter.init(Cipher.DECRYPT_MODE, key);
      byte[] decryptedOutput = decrypter.doFinal(Base64.getDecoder().decode(toDecrypt));
      return new String(decryptedOutput);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return null;
  }
}
