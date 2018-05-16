/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.servlet;

import at.aems.webserver.beans.UserBean;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Niggi
 */
public class JsonFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
	UserBean user = (UserBean) ((HttpServletRequest) request).getSession().getAttribute("user");

	if (user == null || user.getUserId() != 215) {
	    String g = ((HttpServletRequest) request).getParameter("matura");
	    if (g == null || !g.equals("fisch")) {
		((HttpServletResponse) response).sendError(404);
	    } else {
		chain.doFilter(request, response);
	    } 
	} else { 
	    chain.doFilter(request, response);
	}

    }

    @Override
    public void destroy() {
    }

}
