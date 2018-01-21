/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.action;

import at.aems.adminserver.beans.display.AbstractDisplayBean;
import at.aems.adminserver.beans.display.EnquiriesBean;
import at.aems.adminserver.beans.display.NotifyBean;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class DeleteAdminBean extends AbstractActionBean {
    
    private String username;

    public DeleteAdminBean() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String doDelete() {
        notify.setMessage("Admin wurde gelöscht.");
        return "administration";
    }
     
    
}
