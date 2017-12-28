/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.webserver.AemsAPI;
import at.aems.webserver.data.ApiAction;
import at.aems.webserver.data.statistic.NewStatisticData;
import at.aems.webserver.data.warnings.NewWarningData;
import at.aems.webserver.data.warnings.WarningType;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;
 
/**
 *
 * @author Niklas
 */
@ManagedBean(name="newWarning")
@SessionScoped
public class NewWarningBean implements Serializable {
    
    private NewWarningData data;
    
    @ManagedProperty(value = "#{user}")
    private UserBean userBean;
    
    public NewWarningBean() {}
    
    
    @PostConstruct
    public void init() {
        data = new NewWarningData(userBean.getUserId(), userBean.getAuthenticationString());
    }

    public String getName() {
        return data.getName();
    }

    public void setName(String name) {
        data.setName(name);
    }

    public String getType() {
        return "" + data.getType();
    }

    public void setType(String type) {
        data.setType(WarningType.valueOf(type));
    }

    public String getMeters() {
        return "";
    }

    public void setMeters(String meters) {
        data.setMeterIds(AemsAPI.asStringList(meters));
    }

    public String getExceptionDays() {
        return String.join(";", data.getExceptionDays());
    }

    public void setExceptionDays(String exceptionDays) {
        data.setExceptionDays(AemsAPI.asStringList(exceptionDays));
    }

    public String getExceptionDates() {
        return String.join(";", data.getExceptionDates());
    }

    public void setExceptionDates(String exceptionDates) {
        data.setExceptionDates(AemsAPI.asStringList(exceptionDates));
    }

    public String getVariance() {
        return String.valueOf(data.getVariance());
    }

    public void setVariance(String variance) {
        data.setVariance(Integer.parseInt(variance));
    }

    public String doAddWarning() {
        System.out.print(data.toJson());
        return "warnungen";
    }

    public NewWarningData getData() {
        return data;
    }

    public void setData(NewWarningData data) {
        this.data = data;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
    
    
    
    
    
    
}
