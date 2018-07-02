/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.servlet;

import at.aems.adminserver.Constants;
import at.aems.adminserver.beans.UserBean;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import com.google.gson.JsonArray;
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
public class UserExistsServlet extends HttpServlet {

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
	response.setContentType("text/html;charset=UTF-8");
	try (PrintWriter out = response.getWriter()) {
	   
	    UserBean userBean = (UserBean) request.getSession().getAttribute("userBean");
	    if(userBean == null || !userBean.isLoggedIn()) {
		out.print("Insufficient authentication!");
		return;
	    }
	    
	    String user = request.getParameter("user");
	    if(user == null) {
		out.println("No username supplied");
		return;
	    }
	    
	    AemsAPI.setUrl(Constants.API_URL);
	    String query = "{ users(username: \"" + user + "\") { id } }";
	    AemsQueryAction qryAction = new AemsQueryAction(userBean.getAemsUser());
	    
	    AemsResponse resp = AemsAPI.call0(qryAction, null);
	    JsonArray usersArray = resp.getJsonArrayWithinObject();
	    
	    // Check if query returned a user
	    if(usersArray.size() == 0) {
		out.print(false); // User with that username does not exist
	    } else {
		out.print(true); // User with that username exists
	    }
	    
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

}
