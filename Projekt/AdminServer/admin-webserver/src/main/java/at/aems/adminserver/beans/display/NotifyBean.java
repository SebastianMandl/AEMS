/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.display;

import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class NotifyBean {
    
    private String message = null;

    public NotifyBean() {
    }

    public String getMessage() {
        String msg = message;
        message = null;
        return msg;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean hasMessage() {
        return message != null;
    }
}
