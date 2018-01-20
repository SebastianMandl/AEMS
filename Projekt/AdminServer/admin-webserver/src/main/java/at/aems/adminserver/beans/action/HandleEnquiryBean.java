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
        notify.setMessage("Benutzer " + email + " wurde Zugriff gewährt!");
        return "index";
    }
    
    public String doDeny() {
        System.out.println(email + " denied: " + denyMessage);
        notify.setMessage("Dem Benutzer mit der Adresse " + email + " wurde der Zugriff verweigert");
        return "index";
    }
    
    
}
