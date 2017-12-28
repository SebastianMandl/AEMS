/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.webserver.AemsAPI;
import at.aems.webserver.data.AemsAction;
import com.google.gson.JsonObject;
import java.io.Serializable;
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
    
    private AemsAction notificationData;
    private AemsAction notificationMeterData;
    private AemsAction notificationExceptionData;
    
    private static final String TABLE = "Notifications";
    
    @ManagedProperty(value = "#{user}")
    private UserBean userBean;
    
    public NewWarningBean() {}
    
    @PostConstruct
    public void init() {
        notificationData = new AemsAction(userBean.getUserId(), userBean.getAuthenticationString(), "INSERT");
        notificationMeterData = new AemsAction(userBean.getUserId(), userBean.getAuthenticationString(), "INSERT");
        notificationExceptionData = new AemsAction(userBean.getUserId(), userBean.getAuthenticationString(), "INSERT");
        
        notificationData.addTable(TABLE);
        notificationMeterData.addTable("NotificationMeters");
        notificationExceptionData.addTable("NotificationExceptions");
    }

    public String getName() {
        return (String) notificationData.getTable(TABLE).get("name");
    }

    public void setName(String name) {
        notificationData.getTable(TABLE).put("name", name); 
    }

    public String getType() {
        return (String) notificationData.getTable(TABLE).get("type");
    }

    public void setType(String type) {
        notificationData.getTable(TABLE).put("type", type);
    }

    public String getMeters() {
        return null;
    }

    public void setMeters(String meters) {
        
    }

    public String getExceptionDays() {
        return null;
    }

    public void setExceptionDays(String exceptionDays) {
        
    }

    public String getExceptionDates() {
        return null;
    }

    public void setExceptionDates(String exceptionDates) {
        
    }

    public String getVariance() {
        return null;
    }

    public void setVariance(String variance) {
        
    }


    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
    
    
    public String doAddWarning() {
        notificationData.getTable(TABLE).put("user", userBean.getUserId());
        
        JsonObject response = AemsAPI.doPost(notificationData.toJson()).getAsJsonObject();
        int notificationId = response.get("id").getAsInt();
        
        System.out.print(notificationData.toJson());
        return "warnungen";
    }
    
}
