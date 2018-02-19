/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsLoginAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.NewMap;
import at.aems.webserver.beans.action.AbstractActionBean;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import jdk.nashorn.internal.codegen.MapCreator;

/**
 * This managed bean class is designated to serve as the login interface by calling
 * the #doLogin() method.
 * @author Niklas
 */
@ManagedBean(name="login")
@RequestScoped
public class LoginBean extends AbstractActionBean { // Serializeable to allow application to run across multiple server nodes
    private String username;
    private String password;
    
    @ManagedProperty(value = "#{user}")
    private UserBean userBean;
    
    public LoginBean() {
        
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String doLogin() {
	AemsLoginAction login = new AemsLoginAction(EncryptionType.SSL);
	login.setUsername(username);
	login.setPassword(password);
	AemsResponse response = null;
	try {
	    AemsAPI.setUrl(AemsUtils.API_URL);
	    response = AemsAPI.call0(login, null);
	} catch(IOException e) {
	    Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
	}
	
	if(response == null) {
	    notify.setMessage("Es ist ein Fehler aufgetreten!");
	    return "index.xhtml";
	}
	
	JsonObject responseObj = response.getAsJsonObject();
	if(responseObj.has("error")) {
	    notify.setMessage(response.getErrorMessage());
	    return "index.xhtml";
	}
	int id = responseObj.get("id").getAsInt();
	userBean.setUserId(id);
	userBean.setUsername(username);
	userBean.setPassword(password);
	
        FacesContext context = FacesContext.getCurrentInstance();
        String viewId = context.getViewRoot().getViewId();
        return viewId;
    }
    
    public String doLogout() {
        userBean.setUserId(-1);
        userBean.setUsername(null);
        userBean.setPassword(null);
	FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index";
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
    
    
    
}
