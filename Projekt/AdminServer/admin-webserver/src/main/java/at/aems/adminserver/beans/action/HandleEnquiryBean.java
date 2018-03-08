/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.action;

import at.aems.adminserver.UserRole;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsUpdateAction;
import at.aems.apilib.crypto.EncryptionType;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class HandleEnquiryBean extends AbstractActionBean {
    
    private String email;
    private String denyMessage;

    public HandleEnquiryBean() {
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getDenyMessage() {
        return denyMessage;
    }

    public void setDenyMessage(String denyMessage) {
        this.denyMessage = denyMessage;
    }
    
    public String doAccept() {
	
	AemsUpdateAction update = new AemsUpdateAction(userBean.getAemsUser(), EncryptionType.SSL);
	update.setTable("Users");
	update.setIdColumn("email", email);
	
	update.write("role", UserRole.MEMBER.getId());
	
	try {
	    AemsAPI.call0(update, null);
	    notify.setMessage("Benutzer " + email + " wurde Zugriff gewährt!");
	} catch(Exception ex) {
	    notify.setMessage("Es ist ein Fehler aufgetreten");
	}
        callUpdateOn("enquiriesBean");
        return "index";
    }
    
    public String doDeny() {
        System.out.println(email + " denied: " + denyMessage);
        notify.setMessage("Dem Benutzer mit der Adresse " + email + " wurde der Zugriff verweigert");
        callUpdateOn("enquiriesBean");
        return "index";
    }

    
    
}
