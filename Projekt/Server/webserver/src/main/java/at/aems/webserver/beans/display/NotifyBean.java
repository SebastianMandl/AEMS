/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.display;

import at.aems.webserver.beans.objects.NotifyType;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class NotifyBean {
    
    private String message = null;
    private NotifyType notifyType = NotifyType.INFO;

    public NotifyBean() {
    }

    public String getMessage() {
        String msg = message;
        message = null;
        return msg;
    }
    
    public String getType() {
	return this.notifyType.name().toLowerCase();
    }

    public void setMessage(String message) {
        setMessage(message, NotifyType.INFO);
    }
    
    public void setMessage(String message, NotifyType type) {
        this.message = message;
	this.notifyType = type;
    }
    
    public boolean hasMessage() {
        return message != null;
    }
}
