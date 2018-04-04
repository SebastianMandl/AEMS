/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aems;

import aems.database.AEMSDatabase;
import aems.database.ResultSet;
import at.htlgkr.aems.util.crypto.KeyUtils;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sebastian
 */
public class AESKeyManager {
    
    // ip address to key
    private static final HashMap<String, BigDecimal> KEYS = new HashMap<>();
    private static final ArrayList<String[]> PROJECTION = new ArrayList<>();
    private static final HashMap<String, String> SELECTION = new HashMap<>();
    
    static {
        PROJECTION.add(new String[]{ AEMSDatabase.Users.USERNAME });
    }
    
    public static boolean containsKey(String address) {
        return KEYS.containsKey(address);
    }
    
    public static void addKey(String address, byte[] key) {
        KEYS.put(address, new BigDecimal(new String(key)));
    }
    
    public static BigDecimal getSaltedKey(String address, String username) {
        SELECTION.clear();
        SELECTION.put(AEMSDatabase.Users.USERNAME, username);
        ArrayList<String[]> projection = new ArrayList<>(); // can be created at a local scope since this method is only run at login
        projection.add(new String[]{ AEMSDatabase.Users.ID });
        try {
            ResultSet set = DatabaseConnectionManager.getDatabaseConnection().select("aems", AEMSDatabase.USERS, projection, SELECTION);
            return getSaltedKey(address, set.getInteger(0, 0), username);
        } catch (SQLException ex) {
            Logger.getLogger(AESKeyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public static BigDecimal getSaltedKey(String address, int userId) {
        return getSaltedKey(address, userId, null);
    }
    
    public static BigDecimal getSaltedKey(String address, int userId, String username) {
        try {
            if(username == null) {
                SELECTION.clear();
                SELECTION.put(AEMSDatabase.Users.ID, String.valueOf(userId));

                ResultSet set = DatabaseConnectionManager.getDatabaseConnection().select("aems", AEMSDatabase.USERS, PROJECTION, SELECTION);
                username = set.getString(0, 0);
            }

            IPtoID.registerIPtoIDMapping(address, String.valueOf(userId));
            
            BigDecimal key = KEYS.get(address);
            
            String password = DatabaseConnectionManager.getDatabaseConnection().callFunction("aems", "get_user_password", String.class, new Object[]{ username });
            
            return KeyUtils.salt(key, username, password); // for pwd make a function call ; out of commission due to non functioning pljava in postgres.
        } catch (SQLException ex) {
            Logger.getLogger(AESKeyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
