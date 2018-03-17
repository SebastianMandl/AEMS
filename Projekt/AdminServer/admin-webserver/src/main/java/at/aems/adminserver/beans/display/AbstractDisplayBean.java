/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.display;

import at.aems.adminserver.Constants;
import at.aems.adminserver.beans.UserBean;
import static at.aems.adminserver.beans.action.AbstractActionBean.config;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.ApiConfig;
import java.io.File;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.ServletContext;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public abstract class AbstractDisplayBean implements Serializable {

    public AbstractDisplayBean() {
    }
    
    @ManagedProperty(value="#{userBean}")
    protected UserBean userBean;
     
    @PostConstruct
    public void init() {
        update();
    }
    
    /**
     * This method is used to update the contents of whatever this bean 
     * displays. Most likely, interaction with the Aems-API is required
     */
    public abstract void update();

    public UserBean getUserBean() {
	return userBean;
    }

    public void setUserBean(UserBean userBean) {
	this.userBean = userBean;
    }
    
    public void configureApiParams() {
	ServletContext servlet = (ServletContext) FacesContext.getCurrentInstance()
		.getExternalContext().getContext();
	String path = servlet.getRealPath("/") + "cert/certificate.cert";

	File f = new File(path);
	
	TrustManager[] trustAllCerts = new TrustManager[]{
	    new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		    return null;
		}

		public void checkClientTrusted(
			java.security.cert.X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(
			java.security.cert.X509Certificate[] certs, String authType) {
		}
	    }};

	try {
	    SSLContext sc = SSLContext.getInstance("SSL"); 
	    sc.init(null, trustAllCerts, new java.security.SecureRandom());
	    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	} catch (Exception e) {
	}

	config = new ApiConfig(Constants.API_URL, 2500, path, "Minecraft=0");

	AemsAPI.setConfig(config);
    }
    
    
    

}
