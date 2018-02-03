/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.action;

import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class EditStatisticBean {
    
    private Integer id;
    private String name;
    private Integer period;
    private String annotation;

    public EditStatisticBean() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
    
    public String doEdit() {
        System.out.println("   EDIT - " + id + "(" + period + ")");
        return "einstellungenStatistiken";
    }
    
}
