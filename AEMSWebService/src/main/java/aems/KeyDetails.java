/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aems;

import at.htlgkr.aems.util.key.DiffieHellmanProcedure;
import java.math.BigInteger;

/**
 *
 * @author Sebastian
 */
public class KeyDetails {
    
    private static byte[] key;
    
    public static void setKey(byte[] key) throws NullPointerException {
        if(key == null)
            throw new NullPointerException("key cannot be a null reference!");
        KeyDetails.key = key;
    }
    
    public static boolean isKeySet() {
        return getKey() != null;
    }
    
    public static byte[] getKey() {
        BigInteger x = new BigInteger(new String(key));
        x.multiply(new BigInteger(String.valueOf(AuthenticationDetails.getUsername().hashCode())));
        String finalKey = x.toString().substring(0, DiffieHellmanProcedure.KEY_LENGTH);
        
        // todo: write database function
        
        // call database function to retrieve user password
        // salt the users password into the key
        
        return finalKey.getBytes();
    }
    
}
