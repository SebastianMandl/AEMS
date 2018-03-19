/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.action;

import at.aems.webserver.beans.AbstractBean;
import at.aems.webserver.beans.display.AbstractDisplayBean;
import at.aems.webserver.beans.display.NotifyBean;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class AbstractActionBean extends AbstractBean {

    public AbstractActionBean() {
    }

    @ManagedProperty(value = "#{notifyBean}")
    protected NotifyBean notify;

    public NotifyBean getNotify() {
        return notify;
    }

    public void setNotify(NotifyBean notify) {
        this.notify = notify;
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
     
    public void callUpdateOn(String managedBeanName) {
        AbstractDisplayBean bean = getDisplayBean(managedBeanName);
	if(bean != null) {
	    bean.update();
	    System.out.println("Called update() on " + managedBeanName);
	}
	
    }

}
