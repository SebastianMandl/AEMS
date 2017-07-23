package at.htlgkr.aems.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import at.htlgkr.aems.database.AemsMeter.MeterType;
import at.htlgkr.aems.util.Utils;

/**
 * This class provides convenience methods to access data of the AEMS system.
 * It makes use of the {@link at.htlgkr.aems.DatabaseConnection} class.
 * @author Niklas
 *
 */
public class AemsDatabaseHelper {
  
  private DatabaseConnection dbCon;
  
  public AemsDatabaseHelper() {
    dbCon = new DatabaseConnection();
  }
  
  /**
   * Gets all users from the aems database, the passwords will already be decrypted
   */
  public List<AemsUser> getUsers() {
    List<AemsUser> result = new ArrayList<AemsUser>();
    result.add(new AemsUser(0, "Some", "User"));
    return result;
  }
  
  public void insertMeterData(MeterValue value) {
    try {
      
      if(databaseHasValue(value)) {
        return; // Do not overwrite values that already exist
      }
      
      HashMap<String, String> projection = new HashMap<String, String>();
      
      projection.put(AEMSDatabase.MeterDatas.METER.name(), value.getId());
      projection.put(AEMSDatabase.MeterDatas.TIMESTAMP.name(),
          "" + Utils.toPostgresTimestamp(value.getDate()));
      projection.put(AEMSDatabase.MeterDatas.MEASUREDVALUE.name(), "" + value.getValue());
      projection.put(AEMSDatabase.MeterDatas.TEMPERATURE.name(), null);
      // TODO: Temperature
      
      dbCon.insert("aems", "MeterDatas", projection);
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Checks if an entry represented by the <code>value</code> argument already exists.
   * @param value
   * @return true if a matching value already exists, false otherwise
   */
  private boolean databaseHasValue(MeterValue value) {
    
    ArrayList<String[]> projection = new ArrayList<String[]>();
    String idColumn = AEMSDatabase.MeterDatas.ID.name(); // Doesn't matter which column
    projection.add(new String[] {idColumn});
    
    HashMap<String, String> selection = new HashMap<String, String>();
    selection.put(AEMSDatabase.MeterDatas.TIMESTAMP.name(), 
        "" + Utils.toPostgresTimestamp(value.getDate()));
    selection.put(AEMSDatabase.MeterDatas.METER.name(), value.getId());
    
    // SELECT ID FROM aems.MeterDatas 
    // WHERE METER = value.meter AND TIMESTAMP = value.timestamp
    
    try {
      ResultSet result = dbCon.select("aems", "MeterDatas", projection, selection, true);
      return result.iterator().hasNext();
    } catch(SQLException e) {
      return true;
    }
  }
  
  public List<AemsMeter> getMetersOfUser(AemsUser user) {
    List<AemsMeter> result = new ArrayList<AemsMeter>();
    // SELECT * FROM Meters WHERE user_id = user.getId()
    AemsMeter demoMeter = new AemsMeter("AT123", MeterType.ELECTRIC, new AemsLocation(20.2, 20.5));
    result.add(demoMeter);
    return result;
  }

  /**
   * Closes the database connection
   * @return true if the connection was sucessfully closed, false if an error occured
   */
  public boolean close() {
    try {
      dbCon.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
  
  public boolean open(String user, String pass) {
    try {
      dbCon.open(user, pass);
      return true;
    } catch(SQLException e) {
      return false;
    }
  }
  
}
