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
    
    
    public String doAddWarning() {
        try {
            System.out.println(" ========================== ");
	    System.out.println(name + " " + this.meterIds.size() + " " + this.type);
	    System.out.println(this.variance);
	    System.out.println(" ========================== ");
	    
	    
	    String t = this.type == 0 ? "Benachrichtugung" : "Warnung";
	    notify.setMessage(t + " wurde erstellt!");
        } catch (Exception ex) {
            Logger.getLogger(NewWarningBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "einstellungenWarnungen";
    }
    
}
