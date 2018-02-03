/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.servlet;

import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.beans.UserBean;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Niggi
 */
public class EditServlet extends HttpServlet {

    public static final String TYPE_STATISTIC = "s";
    public static final String TYPE_REPORT = "r";
    public static final String TYPE_NOTIFICATION = "n";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        UserBean user = (UserBean) request.getSession().getAttribute("user");

        if (user == null || user.getUserId() == -1) {
            writer.write(jsonError("No session"));
            return;
        }

        String type = request.getParameter("type");
        String id = request.getParameter("id");
        
        if(type == null || id == null) {
            writer.write(jsonError("type and id attributes must be supplied"));
            return;
        }
        
        String result = handleEdit(user.getAemsUser(), id, type);
        writer.write(result);
    }

    private String jsonError(String msg) {
        return "{\"error\": \"" + msg + "\"}";
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
        processRequest(request, response);
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

    private String handleEdit(AemsUser user, String id, String type) {
        
        JsonObject result = new JsonObject();
        
        AemsQueryAction q = new AemsQueryAction(user, EncryptionType.SSL);
        
        if(type.equals(TYPE_STATISTIC)) {
            result.addProperty("name", "Heisenberg");
            result.addProperty("period", 2);
            result.addProperty("annotation", "Eine tolle Statistik!");
        }
        
        return new Gson().toJson(result);
        
    }

}
