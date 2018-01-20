/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.action;

import at.aems.adminserver.beans.display.NotifyBean;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class DeleteAdminBean {
    
    private String username;
    
    @ManagedProperty(value="#{notifyBean}")
    private NotifyBean notify;

    public DeleteAdminBean() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public NotifyBean getNotify() {
        return notify;
    }

    public void setNotify(NotifyBean notify) {
        this.notify = notify;
    }
    
    public String doDelete() {
        notify.setMessage("Admin wurde gelöscht.");
        System.out.println(username);
        return "administration";
    }
     
    
}
