/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.statistic;

import com.google.gson.JsonObject;
import java.util.List;

/**
 *
 * @author Niggi
 */
public class StatisticMeta {
    
    private Integer id;
    private String name;
    private Period period;
    private String annotation;
    private List<String> meters;
    private List<Anomaly> anomalies;
    private boolean isAndroid;
    private boolean isStartpage;

    public StatisticMeta(Integer id, String name) {
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

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public List<String> getMeters() {
        return meters;
    }

    public void setMeters(List<String> meters) {
        this.meters = meters;
    }

    public List<Anomaly> getAnomalies() {
        return anomalies;
    }

    public void setAnomalies(List<Anomaly> anomalies) {
        this.anomalies = anomalies;
    }

    public boolean isAndroid() {
        return isAndroid;
    }

    public void setAndroid(boolean isAndroid) {
        this.isAndroid = isAndroid;
    }

    public boolean isStartpage() {
        return isStartpage;
    }

    public void setStartpage(boolean isStartpage) {
        this.isStartpage = isStartpage;
    }
    
    
    
}
