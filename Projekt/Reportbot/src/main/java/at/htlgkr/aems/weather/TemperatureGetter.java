package at.htlgkr.aems.weather;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONObject;

import at.htlgkr.aems.database.AemsAPI;
import at.htlgkr.aems.database.AemsMeter;
import at.htlgkr.aems.database.AemsUser;
import at.htlgkr.aems.main.Main;
import at.htlgkr.aems.util.BotConfiguration;
import at.htlgkr.aems.util.Logger.LogType;

/**
 * This class makes use of the openweathermap API at
 * {@link api.openweathermap.org} to fetch temperature data. This temperature
 * data will considered when calculating electric consumptions
 * @author Niklas
 *
 */
public class TemperatureGetter {
  
  private AemsUser user;
  private final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
  public static final double ABSOLUTE_ZERO = -273.15; // Will be error return value
  
  public TemperatureGetter(AemsUser user) {
    this.user = user;
  }
  
  public void updateTemperatures() {
    try {
      for(AemsMeter meter : user.getMeters()) {
        double temp = getTemperature(meter);
      }
    } catch(Exception e) {
      Main.logger.log(LogType.ERROR, "Error when collecting weather data of user %0%", user.getUsername());
      Main.logger.log(LogType.ERROR, e);
    }
    
  }
  
  private double getTemperature(AemsMeter meter) throws Exception {
    String urlString = BASE_URL + meter.getLocation().getLocationAsQueryString();
    urlString += "&units=metric&APPID=";
    urlString += Main.config.get(BotConfiguration.API_KEY);
    
    URL url = new URL(urlString);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    
    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
      String rawData = readDataFromStream((InputStream) connection.getContent());
      JSONObject response = new JSONObject(rawData);
      JSONObject data = response.getJSONObject("main");
      return data.getDouble("temp");
    } else {
      return ABSOLUTE_ZERO;
    }
  }
  
  /**
   * by Sebastian Mandl
   */
  private String readDataFromStream(InputStream stream) throws IOException {
    StringBuffer buffer = new StringBuffer();
    while (stream.available() > 0) {
        buffer.append((char) stream.read());
    }
    return buffer.toString();
  }
}
