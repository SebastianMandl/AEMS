/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.apilib.AemsInsertAction;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.AemsUtils;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

/**
 * @author Niggi
 */
@ManagedBean
public class NewReportBean {
    
    @ManagedProperty(value = "#{user}")
    private UserBean userBean;
    
    private String name;
    private String annotation;
    private int timePeriod;
    private boolean autoGenerate;
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
        return this.autoGenerate;
    }

    public void setAutoGenerate(boolean autoGenerate) {
        this.autoGenerate = autoGenerate;
    }
    
    public String getAnnotation() {
        return annotation;
    }
    
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
    
    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
    
    public String doCreate() {
        AemsUser user = new AemsUser(userBean.getUserId(), userBean.getUsername(), userBean.getPassword());
        AemsInsertAction action = new AemsInsertAction(user, EncryptionType.SSL);
        action.setTable("Reports"); 
        action.write("name", name);
        action.write("annotation", annotation);
        action.write("period", timePeriod);
        action.write("user", user.getUserId());
        action.endWrite();
        
        System.out.println(action.toJsonObject());
        
        int reportId = 10;
        AemsInsertAction action2 = new AemsInsertAction(user, EncryptionType.SSL);
        action2.setTable("ReportStatistics");
        for(Integer i : statistics) {
            action2.write("report", reportId);
            action2.write("statistic", i);
            action2.endWrite();
        } 
        System.out.println(action2.toJsonObject());
        
        return "einstellungenBerichte";
    }
    
    
    
    
}
