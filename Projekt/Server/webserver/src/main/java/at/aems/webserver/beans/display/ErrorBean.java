/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.display;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public class ErrorBean implements Serializable {

    private String error;
    
    public void setError(String msg) {
	this.error = msg;
    }
    
    public String getError() {
	String err = this.error;
	this.error = null;
	return err;
    }
    
    public boolean hasMessage() {
	return this.error != null;
    }
    
}
