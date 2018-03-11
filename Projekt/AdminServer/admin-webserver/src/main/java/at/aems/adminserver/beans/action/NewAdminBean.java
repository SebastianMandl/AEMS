/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.action;

import at.aems.adminserver.Constants;
import at.aems.adminserver.UserRole;
import at.aems.adminserver.beans.display.NotifyBean;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUpdateAction;
import at.aems.apilib.crypto.EncryptionType;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class NewAdminBean extends AbstractActionBean {

    private String username;
    private boolean asAdmin;
    
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

    
    public String doCreate() {
        String adminStr = asAdmin ? "Admin" : "Sub-Admin";
        
	configureApiParams();
	AemsUpdateAction up = new AemsUpdateAction(userBean.getAemsUser(), EncryptionType.SSL);
	up.setTable("Users");
	up.setIdColumn("username", username);
	up.write("role", asAdmin ? UserRole.ADMIN.getId() : UserRole.SUB_ADMIN.getId());
	
	try {
	    AemsResponse r = AemsAPI.call0(up, null);
	    if(r.getResponseCode() == 200) {
		notify.setMessage("Der Benutzer '" + username + "' wurde als " + adminStr + " festgelegt");
	    } else {
		notify.setMessage("Es ist ein Fehler aufgetreten!");
	    }
	} catch(Exception ex) {
	    notify.setMessage("Fehler!");
	}
	callUpdateOn("adminDisplayBean");
        return "administration";
    }
    
            
    
    
    
}
