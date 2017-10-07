/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aems;

import at.htlgkr.aems.util.key.DiffieHellmanProcedure;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sebastian
 */
public class KeyExchangeService {
    
    public static void start(String ipAddress) {
        try {
            DiffieHellmanProcedure.sendKeyInfos(new Socket(InetAddress.getByName(ipAddress), 9950));
            // DiffieHellmanProcedure.receiveKeyInfos() is called at the client side to intercept the key details
            KeyDetails.setKey(DiffieHellmanProcedure.confirmKey());
        } catch (IOException ex) {
            Logger.getLogger(KeyExchangeService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
