/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aems;

import java.io.IOException;
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
        byte[] key = DiffieHellmanProcedure.receiveKeyInfos();
        AESKeyManager.addKey(req.getLocalAddr(), key);
    }
    
}
