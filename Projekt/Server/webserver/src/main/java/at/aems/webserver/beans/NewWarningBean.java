/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.apilib.AemsInsertAction;
import at.aems.webserver.AemsAPI;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
 
/**
 *
 * @author Niklas
 */
@ManagedBean(name="newWarning")
@SessionScoped
public class NewWarningBean implements Serializable {
    
    private String name;
    private int type;
    private List<String> meterIds = new ArrayList<>();
    private List<String> exceptionDates = new ArrayList<>();
    private List<String> exceptionDays = new ArrayList<>();
    private int variance;
    
    private AemsInsertAction action;
    
    @ManagedProperty(value = "#{user}")
    private UserBean userBean;
    
    public NewWarningBean() {}
    
    @PostConstruct
    public void init() {
        action = new AemsInsertAction(userBean.getUserId(), userBean.getAuthenticationString());
        action.setTable("Notifications");
        action.beginWrite();
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setType(int type) {
        action.write("type", type);
    }
    
    public void setMeters(String meters) {
        this.meterIds = AemsAPI.asStringList(meters);
    }
    
    public String getMeters() {
        return String.join(";", meterIds);
    }
    
    public int getType() {
        return type;
    }

    public void setExceptionDays(String exceptionDays) {
        this.exceptionDays = AemsAPI.asStringList(exceptionDays);
    }
    
    public String getExceptionDays() {
        return String.join(";", exceptionDays);
    }

    public void setExceptionDates(String exceptionDates) {
        this.exceptionDates = AemsAPI.asStringList(exceptionDates);
    }
    
    public String getExceptionDates() {
        return String.join(";", exceptionDates);
    }

    public void setVariance(int variance) {
        this.variance = variance;
    }
    
    public int getVariance() {
        return variance;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
    
    
    
    
    public String doAddWarning() {
        
        AemsInsertAction action = new AemsInsertAction(userBean.getUserId(), userBean.getAuthenticationString());
        action.setTable("Notifications");
        action.beginWrite();
       
        action.write("name", name);
        action.write("type", type);
        action.write("variance", variance);
        
        action.endWrite();
        
        System.out.print(action.toJson());
        
        return "warnungen";
    }
    
}
