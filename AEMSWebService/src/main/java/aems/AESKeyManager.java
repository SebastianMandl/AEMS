/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aems;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 *
 * @author Sebastian
 */
public class AESKeyManager {
    
    // ip address to key
    private static HashMap<String, BigDecimal> KEYS = new HashMap<>();
    
    public static void addKey(String address, byte[] key) {
        KEYS.put(address, new BigDecimal(new String(key)));
    }
    
    public static BigDecimal getSaltedKey(String address, int userId) {
        return KEYS.get(address);
    }
    
}
