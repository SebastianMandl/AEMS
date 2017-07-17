package at.htlgkr.aems.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

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
}
