package at.htlgkr.aems.database;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import at.htlgkr.aems.database.AemsMeter.MeterType;
import at.htlgkr.aems.main.Main;
import at.htlgkr.aems.util.Logger.LogType;


public class AemsAPI {
  
  static {
    initialize();
  }
  
  private static List<AemsUser> userList;
  
  /**
   * This methods initializes this class by populating the {@link #userList}.
   * Therefore, the AMES-API is called.
   */
  private static void initialize() {
    userList = new ArrayList<AemsUser>();
    
    userList.add(new AemsUser(0, "Some", "User"));
    
    try {
      URL url = new URL("https://some.api.aems.at");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      
      if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
        // MUST. DECRYPT. JSON
        JSONArray users = new JSONArray(connection.getContent()); 
        AemsUser aemsUser;
        for(int i = 0; i < users.length(); i++) {
          JSONObject user = users.getJSONObject(i);
          
          int id = user.getInt("id");
          String name = user.getString("username");
          String password = user.getString("password");
          
          aemsUser = new AemsUser(id, name, password);
          
          JSONArray meters = user.getJSONArray("meters");
          for(int j = 0; j < meters.length(); j++) {
            JSONObject meter = meters.getJSONObject(j);
            
            String meterId = meter.getString("id");
            MeterType type = MeterType.valueOf(meter.getString("type").toUpperCase());
            AemsLocation location = AemsLocation.fromJsonObject(meter.getJSONObject("location"));
            
            aemsUser.getMeters().add(new AemsMeter(meterId, type, location));
          }
          
          userList.add(aemsUser);
          
        }
        
      }
    } catch(Exception e) {
      Main.logger.log(LogType.WARN, "Error when initializing users list in AemsAPI! See log for details");
      e.printStackTrace(Main.logger.getPrinter());
    }
  }
  
  /**
   * Returns a newly instantiated List of all AemsUsers and their meters
   * @return A list of AemsUsers
   */
  public static List<AemsUser> getAemsUsers() {
    return new ArrayList<AemsUser>(userList);
  }
  
  public static void insertWeatherData(List<Integer>lol) {
    
  }
}
