package main.webapp.at.htlgkr.aems;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RESTInf
 */
@WebServlet(name="RESTInf", urlPatterns="/RESTInf")
public class RESTInf extends HttpServlet {
	
	//private static final String PROPERTY_TOTAL_CONSUMPTION = "total_consumption";

	private static final long serialVersionUID = -7229604103220368493L;
	//private DatabaseConnection dbCon = new DatabaseConnection();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
	}
	
}
