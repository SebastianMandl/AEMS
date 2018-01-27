/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.webserver.beans.display.NotifyBean;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class AbstractBean {

    public AbstractBean() {
    }
     
    @ManagedProperty(value="#{user}")
    private UserBean userBean;
    
    @ManagedProperty(value="#{notifyBean}")
    protected NotifyBean notify;

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
    
    
    
    
}
