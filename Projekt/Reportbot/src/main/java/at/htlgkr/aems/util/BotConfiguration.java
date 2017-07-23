package at.htlgkr.aems.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class simplifies the use of the configuration file. The configuration file must
 * be located in the same folder as the .jar file and must be named 'bot.properties'. When the
 * jar is executed and the bot.configuration file is not present, it will be created and
 * initiated with default values.
 * @author Niklas
 *
 */
public class BotConfiguration {
  private File configFile;
  private Properties props;
  
  public static final String FILE_STORAGE = "xls-storage";
  public static final String LOGFILE_STORAGE = "logs-folder";
  public static final String LOGGING_ENABLED = "logging-enabled";
  public static final String MAX_USERS = "max-users-at-once";
  public static final String MAX_RETRIES = "max-retries";
  public static final String API_KEY = "openweahermap-apikey";
  
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
        this.props.setProperty(BotConfiguration.MAX_RETRIES, "1");
        this.props.setProperty(BotConfiguration.MAX_USERS, "50");
        this.props.setProperty(BotConfiguration.LOGFILE_STORAGE, "Logs");
        this.props.setProperty(BotConfiguration.LOGGING_ENABLED, "true");
        this.props.setProperty(BotConfiguration.API_KEY, "API-KEY-HERE");
        
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
  
  /**
   * Gets a property from the configuration file. If the key is not present, <code>null</code>
   * will be returned.
   * @param key
   * @return The property value
   */
  public String get(String key) {
    return props.getProperty(key);
  }
  
  /**
   * Gets a property from the configuration file. If the key is not present, the defaultValue
   * will be returned
   * @param key
   * @param defaultValue
   * @return The property value, or the defaultValue if the key was not found
   */
  public String get(String key, String defaultValue) {
    if(props != null && !props.isEmpty()) {
      return props.getProperty(key, defaultValue);
    }
    return defaultValue;
  }
  
  /**
   * Gets a property from the configuration file. If the key is not present, the defaultValue
   * will be returned
   * The result will be converted into a boolean
   * @param key
   * @param defaultValue
   * @return The boolean value of this property
   */
  public boolean getBoolean(String key, boolean defaultValue) {
    return Boolean.valueOf(props.getProperty(key));
  }
  
  /**
   * Gets a property from the configuration file. If the key is not present, the defaultValue
   * will be returned.
   * The result will be converted into an integer. If the value cannot be parsed to an integer,
   * the defaultValue will be returned.
   * @param key
   * @param defaultValue
   * @return The integer value of this property
   */
  public int getInt(String key, int defaultValue) {
    String value = get(key, String.valueOf(defaultValue));
    try {
      return Integer.parseInt(value);
    } catch(NumberFormatException e) {
      return defaultValue;
    }
  }
}
