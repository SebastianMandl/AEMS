/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.action;

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
        notify.setMessage("Benutzer " + email + " wurde Zugriff gewährt!");
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
