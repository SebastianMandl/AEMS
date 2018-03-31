/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.statistic;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Niggi
 */
public class DisplayedStatistic {

    private Integer id;
    private String name;
    private Period period;
    private String[] labels;

    private List<Double> electricityValues;
    private List<Integer> previousValues;

    private Map<String, List<Integer>> anomalyValues = new HashMap<>();

    public DisplayedStatistic(Integer id, String name, Period period, List<Double> electricityValues, List<Integer> previousValues, Map<String, List<Integer>> anomalyValues) {
	this.id = id;
	this.name = name;
	this.period = period;
	this.electricityValues = electricityValues;
	this.previousValues = previousValues;
	this.anomalyValues = anomalyValues;
    }

    public DisplayedStatistic() {
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

    public List<Double> getElectricityValues() {
	return electricityValues;
    }

    public void setElectricityValues(List<Double> electricityValues) {
	List<Double> rounded = new ArrayList<>();
	for (Double doub : electricityValues) {
	    if (doub != null) {
		rounded.add(Math.ceil(doub));
	    } else {
		rounded.add(null);
	    }
	}
	this.electricityValues = rounded;
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

    public static DisplayedStatistic fromJsonObject(JsonObject object) {
	DisplayedStatistic statistic = new DisplayedStatistic();
	String json = object.toString();

	try {
	    Integer id = JsonPath.read(json, "$.id");
	    String name = JsonPath.read(json, "$.name");
	    String periodId = JsonPath.read(json, "$.period.id");
	    Period p = Period.byId(Integer.parseInt(periodId));

	    if (id == null || name == null || periodId == null || p == null) {
		return null;
	    }

	    return new DisplayedStatistic(id, name, p);
	} catch (Exception ex) {
	    Logger.getLogger(DisplayedStatistic.class.getName()).log(Level.SEVERE, "", ex);
	    return null;
	}

    }

}
