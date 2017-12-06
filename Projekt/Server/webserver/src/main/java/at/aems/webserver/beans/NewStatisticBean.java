/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.webserver.data.statistic.NewStatisticData;
import at.aems.webserver.data.statistic.Period;
import java.io.Serializable;
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

    private NewStatisticData data;
    private Period periods;

    @ManagedProperty(value = "#{user}")
    private UserBean userBean;

    public NewStatisticBean() {
    }

    @PostConstruct
    public void init() {
        data = new NewStatisticData(userBean.getUserId(), userBean.getAuthenticationString());
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
        return data.getStatisticName();
    }

    public void setName(String name) {
        data.setStatisticName(name);
    }

    public Period getPeriod() {
        return data.getPeriod();
    }

    public void setPeriod(Period period) {
        this.data.setPeriod(period);
    }

    public String getAnnotation() {
        return data.getAnnotation();
    }

    public void setAnnotation(String annotation) {
        this.data.setAnnotation(annotation);
    }

    public Period[] getPeriods() {
        return Period.values();
    }

    public String doCreate() {
        /* Call API - JSON Body = data.toJson() */
        return "statistiken";
    }

}
