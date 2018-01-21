/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.warnings;

import java.util.List;

/**
 *
 * @author Niggi
 */
public class WarningMeta {
    private Integer id;
    private String name;
    private WarningType type;
    private Integer maxDeviation;
    private List<String> meters;
    private List<Integer> exceptionDays;
    private List<String> exceptionDates;

    public WarningMeta(Integer id, String name) {
        this.id = id;
        this.name = name;
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

    public WarningType getType() {
        return type;
    }

    public void setType(WarningType type) {
        this.type = type;
    }

    public Integer getMaxDeviation() {
        return maxDeviation;
    }

    public void setMaxDeviation(Integer maxDeviation) {
        this.maxDeviation = maxDeviation;
    }

    public List<String> getMeters() {
        return meters;
    }

    public void setMeters(List<String> meters) {
        this.meters = meters;
    }

    public List<Integer> getExceptionDays() {
        return exceptionDays;
    }

    public void setExceptionDays(List<Integer> exceptionDays) {
        this.exceptionDays = exceptionDays;
    }

    public List<String> getExceptionDates() {
        return exceptionDates;
    }

    public void setExceptionDates(List<String> exceptionDates) {
        this.exceptionDates = exceptionDates;
    }
    
    
    
    
}
