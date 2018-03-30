/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsLoginAction;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.beans.action.AbstractActionBean;
import at.aems.webserver.beans.display.StatisticBean;
import at.aems.webserver.beans.display.UserReportBean;
import at.aems.webserver.beans.display.UserStatisticsBean;
import at.aems.webserver.beans.display.UserWarningsBean;
import at.aems.webserver.beans.objects.UserRole;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 * This managed bean class is designated to serve as the login interface by
 * calling the #doLogin() method.
 *
 * @author Niklas
 */
@ManagedBean(name = "login")
@RequestScoped
public class LoginBean extends AbstractActionBean { // Serializeable to allow application to run across multiple server nodes

    private String username;
    private String password;

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
	    configureApiParams();
	    response = AemsAPI.call0(login, null);
	    System.out.println(response.getDecryptedResponse());
	} catch (IOException e) {
	    Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
	}

	if (response == null || !response.isOk()) {
	    notify.setMessage("Login fehlgeschlagen!");
	    return "index.xhtml";
	}

	JsonObject responseObj = response.getAsJsonObject();
	if (responseObj == null || responseObj.has("error")) {
	    notify.setMessage("Login fehlgeschlagen.");
	    return "index.xhtml";
	}
	int userId = responseObj.get("id").getAsInt();
	if (userId != -1) {
	    userBean.setUserId(userId);
	    userBean.setUsername(username);
	    userBean.setPassword(password);
	    int roleId = getUserRole(userId);
	    if (roleId < UserRole.MEMBER.getId()) { 
		userBean.setUserId(-1);
		userBean.setUsername(null);
		userBean.setPassword(null);
		notify.setMessage("Dieser Account wurde noch nicht von einem Admin bestätigt");
	    }

	} else {
	    notify.setMessage("Die Login-Daten sind falsch!");
	}

	/*
	createSessionBean("statisticBean", StatisticBean.class);
	createSessionBean("userReportBean", UserReportBean.class);
	createSessionBean("userStatisticsBean", UserStatisticsBean.class);
	createSessionBean("userWarningsBean", UserWarningsBean.class);
	
	
	callUpdateOn("userReportBean");
	callUpdateOn("statisticBean");
	 */
	callUpdateOn("userMeterBean");
	callUpdateOn("statisticDisplayBean");
	FacesContext context = FacesContext.getCurrentInstance();
	String viewId = context.getViewRoot().getViewId();
	return viewId;
    }

    public String doLogout() {
	userBean.setUserId(-1);
	userBean.setUsername(null);
	userBean.setPassword(null);
	FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
	notify.setMessage("Auf Wiedersehen!");
	return "index";
    }

    private void createSessionBean(String name, Class<?> aClass) {
	try {
	    FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(name, aClass.newInstance());
	} catch (InstantiationException ex) {
	    Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IllegalAccessException ex) {
	    Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    private int getUserRole(int userId) {
	AemsQueryAction qry = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
	qry.setQuery("{ users(id: \"" + userId + "\") { role { id } } }");

	try {
	    AemsAPI.setUrl(AemsUtils.API_URL);
	    AemsResponse r = AemsAPI.call0(qry, null);

	    JsonArray array = r.getJsonArrayWithinObject();
	    int id = array.get(0).getAsJsonObject().get("role").getAsJsonObject().get("id").getAsInt();
	    return id;
	} catch (Exception ex) {
	    Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
	}

	return -1;
    }

}
