/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.action;

import at.aems.adminserver.UserRole;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsDeleteAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUpdateAction;
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
        
        AemsUpdateAction revoke = new AemsUpdateAction(userBean.getAemsUser(), EncryptionType.SSL);
        revoke.setTable("Users");
        revoke.setIdColumn("email", email);
	revoke.write("role", UserRole.UNREGISTERED.getId());
	
	try {
	    AemsResponse r = AemsAPI.call0(revoke, null);
	    if(r.getResponseCode() == 200) {
		notify.setMessage("Benutzer wurde entfernt!");
	    } 
	} catch(Exception ex) {
	    notify.setMessage("Fehler!");
	}
        
        callUpdateOn("acceptedUsersBean");
	callUpdateOn("enquiriesBean");
        
        return "index";
    }
}
