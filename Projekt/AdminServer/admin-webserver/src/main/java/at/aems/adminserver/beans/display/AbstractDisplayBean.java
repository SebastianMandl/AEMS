/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.display;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niggi
 */
@ManagedBean
public abstract class AbstractDisplayBean {

    public AbstractDisplayBean() {
    }
    
    @PostConstruct
    public void init() {
        update();
    }
    
    /**
     * This method is used to update the contents of whatever this bean 
     * displays. Most likely, interaction with the Aems-API is required
     */
    public abstract void update();
    

}
