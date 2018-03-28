package at.aems.webserver.beans.objects;


import javax.faces.bean.ManagedBean;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sebastian
 */
@ManagedBean
public class Anomaly {
    
    private int id;
    private String meter;
    private String sensor;
    private String script;
    private int cycleTime;
    private String scriptErrors;

    public int getId() {
        return id;
    }

    public void setId(int id) { 
        this.id = id;
    }

    public String getMeter() {
        return meter;
    }

    public void setMeter(String meter) {
        this.meter = meter;
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public int getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(int cycleTime) {
        this.cycleTime = cycleTime;
    }

    public String getScriptErrors() {
        return scriptErrors;
    }

    public void setScriptErrors(String scriptErrors) {
        this.scriptErrors = scriptErrors;
    }
    
    
    
}
