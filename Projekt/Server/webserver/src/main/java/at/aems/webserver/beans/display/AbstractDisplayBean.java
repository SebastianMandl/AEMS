/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.display;

import at.aems.webserver.beans.AbstractBean;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public abstract class AbstractDisplayBean extends AbstractBean implements Serializable{

    public AbstractDisplayBean() {
    }
    
    @PostConstruct
    public void init() {
        onLoad();
    }
    
    /**
     * This method is executed after the bean is constructed. By default,
     * the update() method is called. If something spechial needs to be done,
     * just override this method.
     */
    public void onLoad() {
        update();
    }
    
    /**
     * This method is used to update the data this bean is displaying
     */
    public abstract void update();
}
