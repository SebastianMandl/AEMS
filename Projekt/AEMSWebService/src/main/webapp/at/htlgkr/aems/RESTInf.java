package main.webapp.at.htlgkr.aems;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.webapp.at.htlgkr.aems.database.DatabaseConnection;

/**
 * Servlet implementation class RESTInf
 */
@WebServlet(name="RESTInf", urlPatterns="/RESTInf/*")
public class RESTInf extends HttpServlet {

	private static final long serialVersionUID = -7229604103220368493L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		DatabaseConnection con = new DatabaseConnection();
		try {
			con.open("postgres", "9cc6c23bb4f506bf92111b2601c98e7b");
		} catch (SQLException e) {
			resp.getWriter().write(e.getMessage());
		}
		
		HashMap<String, String> map = new HashMap<>();
		map.put("id", req.getHeader("input"));
		map.put("displayname", "Test");
		
		HashMap<String, String> where = new HashMap<>();
		where.put("id", req.getHeader("input")); 
		where.put("displayname", "Electricity Meter");
		try {
			con.update("aems", "metertypes", map, where);
		} catch (SQLException e) {
			resp.getWriter().write(e.getMessage());
		}
		
		try {
			con.close();
		} catch (SQLException e) {
			resp.getWriter().write(e.getMessage());
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("Put Received!");
		System.out.println(req.getHeader("input"));
	}
	
}
