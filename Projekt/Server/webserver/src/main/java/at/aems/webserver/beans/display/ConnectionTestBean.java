/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.display;

import at.aems.webserver.beans.UserBean;
import java.io.Serializable;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import sun.net.www.protocol.http.HttpURLConnection;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public class ConnectionTestBean implements Serializable {

    @ManagedProperty(value="#{user}") 
    private UserBean userBean;

    public ConnectionTestBean() {
    }

    private String message;
    
    @PostConstruct
    public void update() {
	
	// user is logged in - no check necessary
	if(userBean != null && userBean.isLoggedIn()) {
	    return;
	}
	 
	try {
	    HttpURLConnection con = (HttpURLConnection)
		new URL("http://aemsserver.ddns.net:8084/AEMSWebService").openConnection();
	    con.setReadTimeout(4000);
	    con.setConnectTimeout(4000);
	    con.connect();
	    
	    // this will trigger a SocketTimeoutException if no connection
	    // could be established!
	    con.getResponseCode();
	    
	} catch(Exception e) {
	    message = "Die AEMS-API scheint offline zu sein. Versuchen Sie es später erneut. (" + e.getClass().getSimpleName() + ")";
	    Logger.getLogger(ConnectionTestBean.class.getName()).log(Level.INFO, "No connection to API: {0}", e);
	}
	// Everything went well
    }
    
    public boolean hasMessage() {
	return this.message != null;
    }
    
    public boolean hasConnection() {
	return this.message == null;
    }
    
    public String getMessage() {
	return this.message;
    }
    
    public void setMessage(String msg) {
	this.message = msg;
    }

    public void setUserBean(UserBean userBean) {
	this.userBean = userBean;
    }
    
    
    
}
