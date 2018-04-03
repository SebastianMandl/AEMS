/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aems;

import java.util.HashMap;

/**
 *
 * @author Sebastian
 */
public class IPtoID {
    
    private static final HashMap<String, String> MAP = new HashMap<>();
    
    public static boolean hasMappingFor(String ipAddress) {
        return MAP.containsKey(ipAddress);
    }
    
    public static String convertIPToId(String ipAddress) {
        return MAP.get(ipAddress);
    }
    
    public static void registerIPtoIDMapping(String ipAddress, String id) {
        MAP.put(ipAddress, id);
    }
    
}
