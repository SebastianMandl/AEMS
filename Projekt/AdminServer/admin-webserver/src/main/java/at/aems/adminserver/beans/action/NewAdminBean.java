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
public class NewAdminBean {

    private String username;
    private boolean asAdmin;
    
    @ManagedProperty(value="#{notifyBean}")
    private NotifyBean notify;
    
    public NewAdminBean() {
    }

    public String getUsername() {
        return username;
    }

    public boolean isAsAdmin() {
        return asAdmin;
    }

    public void setAsAdmin(boolean asAdmin) {
        this.asAdmin = asAdmin;
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
    
    public String doCreate() {
        String adminStr = asAdmin ? "Admin" : "Sub-Admin";
        notify.setMessage("Der Benutzer '" + username + "' wurde als " + adminStr + " festgelegt");
        System.out.println(username + " - " + asAdmin);
        return "administration";
    }
    
            
    
    
    
}
