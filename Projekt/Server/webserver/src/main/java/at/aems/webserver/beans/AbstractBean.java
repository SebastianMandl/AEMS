/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.ApiConfig;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.NewMap;
import at.aems.webserver.beans.display.AbstractDisplayBean;
import at.aems.webserver.beans.display.ConnectionTestBean;
import at.aems.webserver.beans.display.NotifyBean;
import java.io.File;
import javax.annotation.PostConstruct;
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
public abstract class AbstractBean {

    public AbstractBean() {
    }
     
    @ManagedProperty(value="#{user}")
    protected UserBean userBean;

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
	
	AbstractDisplayBean bean = getDisplayBean("connectionTestBean");
	int timeout = 5000;
	if(bean != null) {
	    if(((ConnectionTestBean)bean).hasMessage()) {
		timeout = 10;
	    }
	}
 
	ApiConfig config = new ApiConfig(AemsUtils.API_URL, 2000, path, "Minecraft=0");

	AemsAPI.setConfig(config);
    }

    public AbstractDisplayBean getDisplayBean(String managedBeanName) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        Object bean = context.getSessionMap().get(managedBeanName);
	if(bean == null)
	    return null;
        if (bean instanceof AbstractDisplayBean) {
            return (AbstractDisplayBean) bean;
        } else {
            throw new RuntimeException("Bean " + managedBeanName + " is not an instance of AbstractDisplayBean! Object: " + bean);
        }
    }
}
