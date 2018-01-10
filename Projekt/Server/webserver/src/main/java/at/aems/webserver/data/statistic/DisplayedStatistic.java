/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.statistic;

import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Niggi
 */
public class DisplayedStatistic {

    private Integer id;
    private String name;
    private Period period;
    private String[] labels;

    private List<Integer> electricityValues;
    private List<Integer> previousValues = new ArrayList<>(); 

    private Map<String, List<Integer>> anomalyValues = new HashMap<>();

    public DisplayedStatistic(Integer id, String name, Period period, List<Integer> electricityValues, List<Integer> previousValues, Map<String, List<Integer>> anomalyValues) {
        this.id = id;
        this.name = name;
        this.period = period;
        this.electricityValues = electricityValues;
        this.previousValues = previousValues;
        this.anomalyValues = anomalyValues;
    }

    public DisplayedStatistic(Integer id, String name, Period period) {
        this.id = id;
        this.name = name;
        this.period = period;
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

    public List<Integer> getElectricityValues() {
        return electricityValues;
    }

    public void setElectricityValues(List<Integer> electricityValues) {
        this.electricityValues = electricityValues;
    }

    public List<Integer> getPreviousValues() {
        return previousValues;
    }

    public void setPreviousValues(List<Integer> previousValues) {
        this.previousValues = previousValues;
    }

    public Map<String, List<Integer>> getAnomalyValues() {
        return anomalyValues;
    }

    public void setAnomalyValues(Map<String, List<Integer>> anomalyValues) {
        this.anomalyValues = anomalyValues;
    }
    
    public String toJson() {
        labels = period.getLabels();
        return new GsonBuilder()
                .serializeNulls()
                .create().toJson(this);
    }
    
    

}
