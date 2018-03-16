/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.action;

import at.aems.adminserver.Constants;
import at.aems.adminserver.beans.UserBean;
import at.aems.adminserver.beans.display.AbstractDisplayBean;
import at.aems.adminserver.beans.display.NotifyBean;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.ApiConfig;
import java.io.File;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
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
public abstract class AbstractActionBean implements Serializable {

    @ManagedProperty(value = "#{notifyBean}")
    protected NotifyBean notify;

    @ManagedProperty(value = "#{userBean}")
    protected UserBean userBean;

    public static ApiConfig config;

    static {
	
    }

    public AbstractActionBean() {
    }

    public NotifyBean getNotify() {
	return notify;
    }

    public void setNotify(NotifyBean notify) {
	this.notify = notify;
    }

    public UserBean getUserBean() {
	return userBean;
    }

    public void setUserBean(UserBean userBean) {
	this.userBean = userBean;
    }

    public void callUpdateOn(String managedBeanName) {
	AbstractDisplayBean bean = getDisplayBean(managedBeanName);
	if(bean == null) {
	    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, managedBeanName + " is null, cannot call update!");
	} else {
	    getDisplayBean(managedBeanName).update();
	}
	
    }

    public AbstractDisplayBean getDisplayBean(String managedBeanName) {
	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	Object bean = context.getSessionMap().get(managedBeanName);
	if(bean == null) {
	    return null;
	}
	if (bean instanceof AbstractDisplayBean) {
	    return (AbstractDisplayBean) bean;
	} else {
	    throw new RuntimeException("Bean " + managedBeanName + " is not an instance of AbstractDisplayBean!");
	}
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
 
	config = new ApiConfig(Constants.API_URL, 5000, path, "Minecraft=0");

	AemsAPI.setConfig(config);
    }

}
