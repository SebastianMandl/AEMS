/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.warnings;

import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Niggi
 */
public class WarningData {
    private Integer id;
    private String name;
    private Integer type;
    private String meterId;
    private Integer periodId;
    private Integer maxDerivation;
    private List<Integer> exceptionDays = new ArrayList<>();
    
    public static final Integer DEFAULT_PERIOD = 2;

    public WarningData() {
	
    }

    public WarningData(String name, Integer type, String meterId, Integer periodId, List<Integer> exceptionDays) {
	this.name = name;
	this.type = type;
	this.meterId = meterId;
	this.periodId = periodId;
	this.exceptionDays = exceptionDays;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Integer getType() {
	return type;
    }

    public void setType(Integer type) {
	this.type = type;
    }

    public String getMeterId() {
	return meterId;
    }

    public void setMeterId(String meterId) {
	this.meterId = meterId;
    }

    public Integer getPeriodId() {
	return periodId;
    }

    public void setPeriodId(Integer periodId) {
	this.periodId = periodId;
    }

    public List<Integer> getExceptionDays() {
	return exceptionDays;
    }

    public void setExceptionDays(List<Integer> exceptionDays) {
	this.exceptionDays = exceptionDays;
    }

    public Integer getMaxDerivation() {
	return maxDerivation;
    }

    public void setMaxDerivation(Integer maxDerivation) {
	this.maxDerivation = maxDerivation;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }
    
    

    public static WarningData fromJsonObject(JsonObject o) {	
	Integer id = JsonPath.read(o.toString(), "$.id");
	String meterId = JsonPath.read(o.toString(), "$.meter.id");
	String sensorId = JsonPath.read(o.toString(), "$.sensor.id");
	String script = JsonPath.read(o.toString(), "$.script");
	
	if(script == null) {
	    return null;
	}
	
	WarningData data = AemsScript.decompile(script);
	data.setId(id);
	data.setMeterId(meterId == null ? sensorId : meterId);
	data.setPeriodId(WarningData.DEFAULT_PERIOD);
	
	return data;
    }
}
