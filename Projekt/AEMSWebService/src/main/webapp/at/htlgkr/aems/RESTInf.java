package main.webapp.at.htlgkr.aems;

import java.io.IOException;
import java.io.PrintWriter;

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
	
	private static final String PROPERTY_TOTAL_CONSUMPTION = "total_consumption";

	private static final long serialVersionUID = -7229604103220368493L;
	//private DatabaseConnection dbCon = new DatabaseConnection();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		
		// chech weather parameter is present
		String totalConsumption = request.getParameter(PROPERTY_TOTAL_CONSUMPTION);
		if(totalConsumption == null) {
			writer.write("the property \"" + PROPERTY_TOTAL_CONSUMPTION + "\" which was expected could not be found!\n");
			writer.flush();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		float totalConsumptionValue = Float.valueOf(totalConsumption);
		response.setStatus(HttpServletResponse.SC_OK);
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
