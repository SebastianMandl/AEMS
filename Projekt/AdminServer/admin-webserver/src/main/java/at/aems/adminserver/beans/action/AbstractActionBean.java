/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.action;

import at.aems.adminserver.beans.UserBean;
import at.aems.adminserver.beans.display.AbstractDisplayBean;
import at.aems.adminserver.beans.display.NotifyBean;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author Niggi
 */
@ManagedBean
public abstract class AbstractActionBean {

    @ManagedProperty(value = "#{notifyBean}")
    protected NotifyBean notify;
    
    @ManagedProperty(value="#{userBean}")
    protected UserBean userBean;

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
        getDisplayBean(managedBeanName).update();
    }
    
    public AbstractDisplayBean getDisplayBean(String managedBeanName) {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        Object bean = context.getSessionMap().get(managedBeanName);
        if(bean instanceof AbstractDisplayBean) {
            return (AbstractDisplayBean) bean;
        } else {
            throw new RuntimeException("Bean " + managedBeanName + " is not an instance of AbstractDisplayBean!");
        }
    }
    
    
}
