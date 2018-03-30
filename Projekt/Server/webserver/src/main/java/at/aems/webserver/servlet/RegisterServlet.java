/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.servlet;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsDeleteAction;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUpdateAction;
import at.aems.apilib.AemsUser;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.beans.objects.UserRole;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Base64;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Niggi
 */
public class RegisterServlet extends HttpServlet {

    private AemsUser master = new AemsUser(215, "master", "pwd");
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (request.getParameter("data") == null) {
            response.getWriter().write("No data was supplied");
            return;
        }
        try {
            String encodedData = request.getParameter("data");
            String decodedData = new String(Base64.getUrlDecoder().decode(encodedData));
            JsonObject data = new JsonParser().parse(decodedData).getAsJsonObject();

            String email = data.get("e").getAsString();
            String code = data.get("c").getAsString();
	    
	    JsonArray array = getRegistrationWithEmail(email);
	    if(array == null) {
		response.getWriter().write("Error");
		return;
	    }
	    
	    JsonObject registration = array.get(0).getAsJsonObject();
	    if(codesEqual(registration, code)) {
		setVerifiedEmail(email);
		deleteRegistration(email);
		response.getWriter().write("Sie haben ihre E-Mail erfolgreich bestätigt!");
	    } else {
		response.getWriter().write("Error");
	    }
        } catch (Exception e) {
            response.getWriter().write("Error");
            return;
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

    private JsonArray getRegistrationWithEmail(String email) throws IOException {
	    AemsQueryAction query = new AemsQueryAction(master);
	    query.setQuery("{ registrations(email: \"" + email + "\") { timestamp, confirm_code } }");
	    
	    AemsAPI.setUrl(AemsUtils.API_URL);
	    AemsResponse resp = AemsAPI.call0(query, null);
	    
	    JsonArray array = resp.getJsonArrayWithinObject();
	    if(array.size() != 1) {
		return null;
	    }
	    return array;
    }

    private boolean codesEqual(JsonObject registration, String code) throws IOException{
	String regCode = registration.get("confirm_code").getAsString();
	return code.equals(regCode);
    }

    private boolean setVerifiedEmail(String email) throws IOException {
	
	AemsUpdateAction update = new AemsUpdateAction(master);
	update.setTable("users");
	update.setIdColumn("email", email);
	update.write("role", UserRole.UNREGISTERED.getId());
	
	return AemsAPI.call0(update, null).isOk();
	
    }

    private boolean deleteRegistration(String email) throws IOException {
	AemsDeleteAction delete = new AemsDeleteAction(master);
	delete.setTable("registrations");
	delete.setIdColumn("email", email);
	AemsResponse response = AemsAPI.call0(delete, null);
	return response.isOk();
    }

}
