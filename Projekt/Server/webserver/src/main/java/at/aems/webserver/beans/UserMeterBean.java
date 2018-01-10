/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class UserMeterBean {
    private List<String> meterTypes;
    private Map<String, String> meters;
    
    @ManagedProperty(value = "#{user}")
    private UserBean userBean;
    
    public UserMeterBean() {
        
    }
    
    @PostConstruct
    public void init() {
        meterTypes = new ArrayList<>();
        meters = new HashMap<>();
        
        meters.put("AT0001", "Strom");
        meters.put("AT0002", "Strom");
        meters.put("AT0003", "Wasser");
        meters.put("AT0004", "Gas");
        
        meterTypes.add("Strom");
        meterTypes.add("Wasser");
        meterTypes.add("Gas");
    }

    public List<String> getMeterTypes() {
        return meterTypes;
    }

    public void setMeterTypes(List<String> meterTypes) {
        this.meterTypes = meterTypes;
    }

    public Map<String, String> getMeters() {
        return meters;
    }

    public void setMeters(Map<String, String> meters) {
        this.meters = meters;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
    
    
    
}
