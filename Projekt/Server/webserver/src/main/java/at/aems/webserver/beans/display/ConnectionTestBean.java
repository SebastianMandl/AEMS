/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.display;

import java.net.HttpURLConnection;
import java.net.URL;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@RequestScoped
public class ConnectionTestBean extends AbstractDisplayBean {

    private String message;
    
    @Override
    public void update() {
	
	// user is logged in - no check necessary
	if(userBean != null && userBean.isLoggedIn()) {
	    return;
	}
	 
	try {
	    HttpURLConnection con = (HttpURLConnection) 
		new URL("http://aemsserver.ddns.net:8084/AEMSWebService").openConnection();
	    con.setReadTimeout(3000);
	    con.setConnectTimeout(3000);
	    con.connect();
	} catch(Exception e) {
	    message = "Die AEMS-API scheint offline zu sein. Versuchen Sie es später erneut. (" + e.getClass().getSimpleName() + ")";
	}
	// Everything went well
    }
    
    public boolean hasMessage() {
	return this.message != null;
    }
    
    public String getMessage() {
	return this.message;
    }
    
    public void setMessage(String msg) {
	this.message = msg;
    }
    
}
