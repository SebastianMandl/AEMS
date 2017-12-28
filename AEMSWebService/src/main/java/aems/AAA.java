/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aems;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Sebastian
 */
@WebServlet(name = "AAA", urlPatterns = {"/AAA/*"})
public class AAA extends HttpServlet {

    private byte[] key;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    key = DiffieHellmanProcedure.receiveKeyInfos();
                    return;
                } catch (IOException ex) {
                    Logger.getLogger(AAA.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        
        while(key == null);
        AESKeyManager.addKey(req.getRemoteAddr(), key);
    }
    
}
