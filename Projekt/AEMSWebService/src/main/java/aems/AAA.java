/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
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
    
    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {     
        if(req.getParameter("android") != null && req.getParameter("android").equals("android")) {
            byte[] key = "4586974532156889".getBytes();
            resp.getWriter().write("4586974532156889");
            resp.getWriter().flush();
            AESKeyManager.addKey(req.getRemoteAddr(), key);
            return;
        }
        
        byte[] key = DiffieHellmanProcedure.receiveKeyInfos();
        AESKeyManager.addKey(req.getRemoteAddr(), key);  
    }
    
}
