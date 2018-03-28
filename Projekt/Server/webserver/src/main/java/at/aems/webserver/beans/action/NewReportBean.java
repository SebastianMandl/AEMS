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
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

/**
 * @author Niggi
 */
@ManagedBean
public class NewReportBean extends AbstractActionBean {
    
    private String name;
    private String annotation;
    private int timePeriod;
    private boolean instantDownload;
    private List<Integer> statistics;
    
    public NewReportBean() {}

    public String getName() {
        return name;
    }
    
    @PostConstruct
    public void init() {
        
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(int timePeriod) {
        this.timePeriod = timePeriod;
    }

    public String getStatisticIds() {
        return "";
    }

    public void setStatisticIds(String statisticIds) {
        this.statistics = AemsUtils.asIntList(statisticIds);
    }

    public boolean isAutoGenerate() {
        return this.instantDownload;
    }

    public void setAutoGenerate(boolean autoGenerate) {
        this.instantDownload = autoGenerate;
    }
    
    public String getAnnotation() {
        return annotation;
    }
    
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
    
    public String doCreate() {
        AemsUser user = new AemsUser(userBean.getUserId(), userBean.getUsername(), userBean.getPassword());
        AemsInsertAction action = new AemsInsertAction(user, EncryptionType.SSL);
        action.setTable("Reports"); 
        action.write("name", name);
        action.write("annotation", annotation);
//        action.write("auto_generate", autoGenerate);
        action.write("period", timePeriod);
        action.write("user", user.getUserId());
        action.endWrite();
        
        AemsResponse response = null;
	try {
	    configureApiParams();
	    response = AemsAPI.call0(action, null);
	    if(response.getResponseCode() != 200) {
		response = null;
	    }
	} catch(IOException ex) {
	    // failed
	}
	
	if(response == null) {
	    notify.setMessage("Da ist etwas schief gelaufen!");
	    return "einstellungenBerichte";
	}
        
        String reportId = response.getAsJsonObject().get("id").getAsString();
        AemsInsertAction action2 = new AemsInsertAction(user, EncryptionType.SSL);
        action2.setTable("ReportStatistics");
        for(Integer i : statistics) {
            action2.write("report", reportId);
            action2.write("statistic", i);
            action2.endWrite();
        } 
        
	try {
	    AemsResponse response2 = AemsAPI.call0(action2, null);
	} catch(IOException e) {
	    // yes
	}
        
        notify.setMessage("Bericht wurde erstellt!");
	callUpdateOn("userReportBean");
        return "einstellungenBerichte";
    }
    
    
    
    
}
