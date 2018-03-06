/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.display;

import at.aems.adminserver.beans.UserBean;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public abstract class AbstractDisplayBean implements Serializable {

    public AbstractDisplayBean() {
    }
    
    @ManagedProperty(value="#{userBean}")
    protected UserBean userBean;
     
    @PostConstruct
    public void init() {
        update();
    }
    
    /**
     * This method is used to update the contents of whatever this bean 
     * displays. Most likely, interaction with the Aems-API is required
     */
    public abstract void update();

    public UserBean getUserBean() {
	return userBean;
    }

    public void setUserBean(UserBean userBean) {
	this.userBean = userBean;
    }
    
    
    

}
