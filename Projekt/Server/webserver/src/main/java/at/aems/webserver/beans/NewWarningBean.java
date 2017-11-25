/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author Niklas
 */
@ManagedBean(name="newWarning")
@RequestScoped
public class NewWarningBean implements Serializable {
    private String name;
    private String type;
    private String meters;
    private String exceptionDays;
    private String exceptionDates;
    
    public NewWarningBean() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMeters() {
        return meters;
    }

    public void setMeters(String meters) {
        this.meters = meters;
    }

    public String getExceptionDays() {
        return exceptionDays;
    }

    public void setExceptionDays(String exceptionDays) {
        this.exceptionDays = exceptionDays;
    }

    public String getExceptionDates() {
        return exceptionDates;
    }

    public void setExceptionDates(String exceptionDates) {
        this.exceptionDates = exceptionDates;
    }
    
    public String doAddWarning() {
        return "warnungen";
    }
    
    
    
    
}
