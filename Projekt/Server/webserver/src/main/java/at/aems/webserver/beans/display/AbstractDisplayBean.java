/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.display;

import at.aems.webserver.beans.AbstractBean;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public abstract class AbstractDisplayBean extends AbstractBean implements Serializable {

    public AbstractDisplayBean() {
	super();
    }
    
    @ManagedProperty(value="#{errorBean}")
    protected ErrorBean errorBean;
    
    
    @PostConstruct
    public void init() {
	    onLoad();
    }

    /**
     * This method is executed after the bean is constructed. By default,
     * the update() method is called. If something spechial needs to be done,
     * just override this method.
     */
    public void onLoad() {
        update();
    }
    
    /**
     * This method is used to update the data this bean is displaying
     */
    public abstract void update();

    public void setErrorBean(ErrorBean errorBean) {
	this.errorBean = errorBean;
    }

    
    public void requireBean(Class<?> beanClass) {
	// make first letter lower case
	String className = beanClass.getSimpleName();
	char[] chars = className.toCharArray();
	chars[0] = Character.toLowerCase(chars[0]);
	className = new String(chars);
	
	ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
	Map<String, Object> sessionMap = context.getSessionMap();
	if(!sessionMap.containsKey(className)) {
	    try {
		sessionMap.put(className, beanClass.newInstance());
		System.out.println(" * Session bean created: " + className);
	    } catch(Exception ex) {
		throw new RuntimeException(ex);
	    }
	}
    }
    
    
    
    
}
