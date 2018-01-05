/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.webserver.AemsUtils;
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
    
    public NewReportBean() {}

    public String getName() {
        return null;
    }
    
    @PostConstruct
    public void init() {
        
    }

    public void setName(String name) {
        
    }

    public int getTimePeriod() {
        return 0;
    }

    public void setTimePeriod(int timePeriod) {
        
    }

    public String getStatisticIds() {
        return null;
    }

    public void setStatisticIds(String statisticIds) {
        
    }

    public boolean isAutoGenerate() {
        return false;
    }

    public void setAutoGenerate(boolean autoGenerate) {
        
    }
    
    public String getAnnotation() {
        return null;
    }
    
    public void setAnnotation(String annotation) {
        
    }
    
    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
    
    
    
    
}
