/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.action;

import at.aems.adminserver.UserRole;
import at.aems.adminserver.beans.display.AbstractDisplayBean;
import at.aems.adminserver.beans.display.EnquiriesBean;
import at.aems.adminserver.beans.display.NotifyBean;
import at.aems.apilib.AemsDeleteAction;
import at.aems.apilib.AemsUpdateAction;
import at.aems.apilib.crypto.EncryptionType;
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
        AemsUpdateAction delete = new AemsUpdateAction(userBean.getAemsUser(), EncryptionType.SSL);
        delete.setTable("Users");
        delete.setIdColumn("username", username);
        delete.write("role", UserRole.MEMBER.getId());
        callUpdateOn("adminDisplayBean");
        
        return "administration";
    }
     
    
}
