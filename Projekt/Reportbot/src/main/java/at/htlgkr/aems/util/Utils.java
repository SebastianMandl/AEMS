package at.htlgkr.aems.util;

import java.net.URL;

public class Utils {
  public static String removePortFromUrl(URL url) {
    String s = url.toString();
    int port = url.getPort();
    return s.replaceAll(":" + port, "");
  }
}
