/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans;

import at.aems.adminserver.Constants;
import at.aems.adminserver.UserRole;
import at.aems.adminserver.beans.action.AbstractActionBean;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsLoginAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.crypto.EncryptionType;
import com.google.gson.JsonObject;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Niggi
 */
@ManagedBean(name="login")
@RequestScoped
public class LoginBean extends AbstractActionBean { // Serializeable to allow application to run across multiple server nodes
    public static final int LOGGED_OUT_USER_ID = -1;
    private String username;
    private String password;
    
    @ManagedProperty(value = "#{userBean}")
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
	int userId = -1; 
	try { 
	    AemsAPI.setUrl(Constants.API_URL);
	    System.out.println(login.toJson(null));
	    response = AemsAPI.call0(login, null);
	    
	    System.out.print(response.getDecryptedResponse());
	    JsonObject o = response.getAsJsonObject();
	    userId = o.has("id") ? o.get("id").getAsInt() : -1;
	} catch(Exception ex) {
	    Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
	    notify.setMessage("Ein Fehler ist aufgetreten!");
	}
        
        if(userId != -1) {
            userBean.setUserId(userId);
            userBean.setUsername(username);
            userBean.setPassword(password);
            if(username.equals("master")) {
                userBean.setRole(UserRole.ADMIN);
            } else {
                userBean.setRole(UserRole.SUB_ADMIN);
            }
        } else {
	    notify.setMessage("Die Login-Daten sind falsch!");
	}
        FacesContext context = FacesContext.getCurrentInstance();
        String viewId = context.getViewRoot().getViewId();
        return viewId;
    }
    
    public String doLogout() {
        userBean.setUserId(LOGGED_OUT_USER_ID);
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
