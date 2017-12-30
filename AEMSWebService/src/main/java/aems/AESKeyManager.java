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
    
    public static BigDecimal getSaltedKey(String address, int userId) {
        try {
            SELECTION.clear();
            SELECTION.put(AEMSDatabase.Users.ID, String.valueOf(userId));
            
            ResultSet set = DatabaseConnectionManager.getDatabaseConnection().select("aems", AEMSDatabase.USERS, PROJECTION, SELECTION);
            String username = set.getString(0, 0);
            
            BigDecimal key = KEYS.get("0:0:0:0:0:0:0:1");
            return KeyUtils.salt(key, username, "pwd"); // for pwd make a function call ; out of commission due to non functioning pljava in postgres.
        } catch (SQLException ex) {
            Logger.getLogger(AESKeyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
