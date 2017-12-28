package aems;

import aems.database.DatabaseConnection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Sebastian
 */
public class DatabaseConnectionManager {
    
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";
    
    // cache the database connection
    private static DatabaseConnection connection;
    
    public static DatabaseConnection getDatabaseConnection() {
        if(connection == null) {
            try {
                connection = new DatabaseConnection();
                connection.open(USERNAME, PASSWORD);
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return connection;
    }    
    
}
