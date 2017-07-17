package at.htlgkr.aems.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BotConfiguration {
  private File configFile;
  private Properties props;
  
  public static String FILE_STORAGE = "xls-storage";
  public static String ONE_FOLDER_PER_USER = "folder-for-each-user";
  public static String DECRYPTION_KEY = "decrypt-key";
  
  public BotConfiguration() {
    this.configFile = new File("bot.properties");
    this.props = new Properties();
    init();
  }
  
  private void init() {
    try {
      if(!configFile.exists()) {
        configFile.createNewFile();
      }
      InputStream stream = new FileInputStream(configFile);
      this.props.load(stream);
      
      if(props.isEmpty()) {
        this.props.setProperty(BotConfiguration.FILE_STORAGE, "Exceldateien");
        this.props.setProperty(BotConfiguration.ONE_FOLDER_PER_USER, "true");
        this.props.setProperty(BotConfiguration.DECRYPTION_KEY, "MY-SECRET-KEY");
        
        FileWriter writer = new FileWriter(configFile);
        this.props.store(writer, "AEMS Bot Konfigurationsdatei");
        writer.close();
      }
      stream.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public String get(String key) {
    return props.getProperty(key);
  }
  
  public String get(String key, String defaultValue) {
    if(props != null && !props.isEmpty()) {
      return props.getProperty(key, defaultValue);
    }
    return defaultValue;
  }
}
