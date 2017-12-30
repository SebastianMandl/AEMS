/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.webserver.data.statistic.Period;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.SelectItem;

/**
 *
 * @author Niklas
 */
@ManagedBean(name = "newStatistic")
public class NewStatisticBean implements Serializable {

    private Period periods;

    @ManagedProperty(value = "#{user}")
    private UserBean userBean;

    public NewStatisticBean() {
    }

    @PostConstruct
    public void init() {
        
    }

    public SelectItem[] getPeriodValues() {
        SelectItem[] items = new SelectItem[Period.values().length];
        int i = 0;
        for (Period p : Period.values()) {
            items[i++] = new SelectItem(p, p.getLabel());
        }
        return items;
    }

    public void setUserBean(UserBean bean) {
        this.userBean = bean;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public String getName() {
        return null;
    }

    public void setName(String name) {
        
    }

    public Period getPeriod() {
        return null;
    }

    public void setPeriod(Period period) {
       
    }

    public String getAnnotation() {
        return null;
    }

    public void setAnnotation(String annotation) {
       
    }
    
    public void setDates(List<String>dates) {
       
    }

    public Period[] getPeriods() {
        return Period.values();
    }

    public String doCreate() {
        /* Call API - JSON Body = data.toJson() */
        return "einstellungenStatistiken";
    }

}
