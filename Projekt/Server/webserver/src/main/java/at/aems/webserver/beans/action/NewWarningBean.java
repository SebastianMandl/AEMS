/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.action;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsInsertAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.beans.UserBean;
import at.aems.webserver.data.warnings.AemsScript;
import at.aems.webserver.data.warnings.WarningData;
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
   
    
    public NewWarningBean() {}

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
	    
	    List<Integer> exceptions = new ArrayList<>();
	    for(String s : this.exceptionDays) {
		exceptions.add(Integer.valueOf(s));
	    }
	    
	    WarningData data = new WarningData();
	    data.setName(this.name);
	    data.setMaxDerivation(this.variance);
	    data.setMeterId(this.meterIds.get(0));
	    data.setExceptionDays(exceptions);
	    data.setPeriodId(1);
	    data.setType(type);
	    	    
	    AemsInsertAction insert = new AemsInsertAction(userBean.getAemsUser());
	    insert.setTable("anomalies");
	    insert.write("meter", data.getMeterId());
	    insert.write("script", AemsScript.compile(data));
	    insert.write("sensor", "Sensor 1"); 
	    insert.write("exec_intermediate_time", 15); 
	    insert.endWrite();
	    
	    AemsResponse response = AemsAPI.call0(insert, null);
	    if(response == null || response.getResponseCode() != 200) {
		notify.setMessage("Ein Fehler ist aufgetreten");
		return "einstellungenWarnungen";
	    }
	    notify.setMessage("Objekt wurde erfolreich erstellt.");
	    callUpdateOn("userWarningsBean");
        } catch (Exception ex) {
            Logger.getLogger(NewWarningBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "einstellungenWarnungen";
    }
    
}
