/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.webserver.AemsAPI;
import at.aems.webserver.data.reports.NewReportData;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;

/**
 * @author Niggi
 */
@ManagedBean
public class NewReportBean {

    private NewReportData data;
    
    @ManagedProperty(value = "#{user}")
    private UserBean userBean;
    
    public NewReportBean() {}

    public String getName() {
        return data.getName();
    }
    
    @PostConstruct
    public void init() {
        data = new NewReportData(userBean.getUserId(), userBean.getAuthenticationString());
    }

    public void setName(String name) {
        data.setName(name);
    }

    public int getTimePeriod() {
        return data.getTimePeriod();
    }

    public void setTimePeriod(int timePeriod) {
        data.setTimePeriod(timePeriod);
    }

    public String getStatisticIds() {
        return String.join(";", data.getStatisticIds());
    }

    public void setStatisticIds(String statisticIds) {
        data.setStatisticIds(AemsAPI.asStringList(statisticIds));
    }

    public boolean isAutoGenerate() {
        return data.isAutoGenerate();
    }

    public void setAutoGenerate(boolean autoGenerate) {
        data.setAutoGenerate(autoGenerate);
    }
    
    public String getAnnotation() {
        return data.getAnnotation();
    }
    
    public void setAnnotation(String annotation) {
        data.setAnnotation(annotation);
    }

    public NewReportData getData() {
        return data;
    }

    public void setData(NewReportData data) {
        this.data = data;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
    
    
    
    
}
