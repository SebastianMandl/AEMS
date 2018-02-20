/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.action;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsInsertAction;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.beans.UserBean;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
 
/**
 *
 * @author Niklas
 */
@ManagedBean(name="newWarning")
public class NewWarningBean extends AbstractActionBean {
    
    private String name;
    private int type;
    private List<String> meterIds = new ArrayList<>();
    private List<String> exceptionDates = new ArrayList<>();
    private List<String> exceptionDays = new ArrayList<>();
    private int variance;
    
    private AemsInsertAction action;
    
    @ManagedProperty(value = "#{user}")
    private UserBean userBean;
    
    private AemsUser user;
    
    public NewWarningBean() {}
    
    @PostConstruct
    public void init() {
        user = new AemsUser(userBean.getUserId(), userBean.getUsername(), userBean.getPassword());
        action = new AemsInsertAction(user, EncryptionType.SSL);
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    public void setMeters(String meters) {
        this.meterIds = AemsUtils.asStringList(meters);
    }
    
    public String getMeters() {
        return String.join(";", meterIds);
    }
    
    public int getType() {
        return type;
    }

    public void setExceptionDays(String exceptionDays) {
        this.exceptionDays = AemsUtils.asStringList(exceptionDays);
    }
    
    public String getExceptionDays() {
        return String.join(";", exceptionDays);
    }

    public void setExceptionDates(String exceptionDates) {
        this.exceptionDates = AemsUtils.asStringList(exceptionDates);
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
        try {
            AemsInsertAction insertNoti = new AemsInsertAction(user, EncryptionType.SSL);
            insertNoti.setTable("Notifications");
            insertNoti.beginWrite();
            insertNoti.write("user", user.getUserId());
            insertNoti.write("name", name);
            insertNoti.write("type", type);
            insertNoti.write("min_positive_deviation", variance);
            insertNoti.write("min_negative_deviation", variance);
            insertNoti.endWrite();
            
            System.out.println(insertNoti.toJsonObject().toString());
            
            AemsAPI.setUrl(AemsUtils.API_URL); 
            
            //String response = AemsUtils.decodeBase64(AemsAPI.call(insertNoti, null));
            int notificationId = 10;//AemsUtils.getResponseId(response);
            
            AemsInsertAction insertMeters = new AemsInsertAction(user, EncryptionType.SSL);
            insertMeters.setTable("NotificationMeters");
            for(String meter : this.meterIds) {
                insertMeters.write("meter", meter);
                insertMeters.write("notification", notificationId);
                insertMeters.endWrite();
            }
            System.out.println(insertMeters.toJsonObject().toString());
            AemsAPI.call(insertMeters, null);
            
            
            AemsInsertAction insertExceptions = new AemsInsertAction(user, EncryptionType.SSL);
            insertExceptions.setTable("NotificationExceptions");
            insertExceptions.beginWrite();
            for(String day : exceptionDays) {
                insertExceptions.write("notification", notificationId);
                insertExceptions.write("week_day", Integer.valueOf(day));
                insertExceptions.write("min_positive_deviation", 90);
                insertExceptions.write("min_negative_deviation", 90);
                insertExceptions.endWrite();
            }
            for(String date : exceptionDates) {
                insertExceptions.write("notification", notificationId);
                insertExceptions.write("exception_date", Integer.valueOf(date));
                insertExceptions.write("min_positive_deviation", 90);
                insertExceptions.write("min_negative_deviation", 90);
                insertExceptions.endWrite();
            }
            
            AemsAPI.call(insertExceptions, null);
            System.out.print(insertNoti.toJson(null));
            callUpdateOn("userWarningsBean");

        } catch (IOException ex) {
            Logger.getLogger(NewWarningBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "einstellungenWarnungen";
    }
    
}
