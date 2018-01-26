/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.servlet;

import at.aems.adminserver.beans.UserBean;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.crypto.EncryptionType;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.faces.bean.ManagedProperty;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
 
/**
 *
 * @author Niggi
 */
public class QueryServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        UserBean bean = (UserBean) request.getSession().getAttribute("userBean");
        
        try (PrintWriter out = response.getWriter()) {
            if(bean == null || !bean.isLoggedIn()) {
                out.println(jsonError("No session available"));
                return;
            }
            String query = request.getParameter("q");
            if(query == null) {
                out.println(jsonError("No query was supplied."));
                return; 
            }
            AemsQueryAction queryAction = new AemsQueryAction(bean.getAemsUser(), EncryptionType.SSL);
            // Echo back query
            out.print("{\"q\": \"" + query + "\"}");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            response.setStatus(400);    // bad request
            out.print(jsonError("QueryServlet does not accept GET requests"));
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private String jsonError(String msg) {
        return "{\"error\": \"" + msg + "\"}";
    }
    
}
