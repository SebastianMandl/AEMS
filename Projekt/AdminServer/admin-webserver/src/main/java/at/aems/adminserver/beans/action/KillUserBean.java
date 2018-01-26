/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.action;

import at.aems.apilib.AemsDeleteAction;
import at.aems.apilib.crypto.EncryptionType;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class KillUserBean extends AbstractActionBean {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String doRevoke() {
        
        AemsDeleteAction delete = new AemsDeleteAction(userBean.getAemsUser(), EncryptionType.SSL);
        delete.setTable("Users");
        delete.setIdColumn("email", email);
        
        notify.setMessage("Benutzer wurde entfernt!");
        
        callUpdateOn("adminDisplayBean");
        
        return "index";
    }
}
