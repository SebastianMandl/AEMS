package at.htlgkr.aems.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides convenience methods to access data about users of the AEMS system.
 * It makes use of the {@link at.htlgkr.aems.DatabaseConnection} class.
 * @author Niklas
 *
 */
public class AemsDatabaseHelper {
  
  private DatabaseConnection dbCon;
  
  public AemsDatabaseHelper() {
    dbCon = new DatabaseConnection();
  }
  
  public List<AemsUser> getUsers() {
    // Get id, username and password from database
    // For each user, decrypt the password
    // Add user to a "result" list
    List<AemsUser> result = new ArrayList<AemsUser>();
    result.add(new AemsUser(0, "Some", "User"));
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
