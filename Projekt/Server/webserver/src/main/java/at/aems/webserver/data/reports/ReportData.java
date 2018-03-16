/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.reports;

import com.google.gson.JsonObject;

/**
 *
 * @author Niggi
 */
public class ReportData {
    private Integer id;
    private String name;
    private Integer periodId;
    private String annotation;

    public ReportData() {
    }

    public ReportData(Integer id, String name, Integer periodId, String annotation) {
	this.id = id;
	this.name = name;
	this.periodId = periodId;
	this.annotation = annotation;
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

    public Integer getPeriodId() {
	return periodId;
    }

    public void setPeriodId(Integer periodId) {
	this.periodId = periodId;
    }

    public String getAnnotation() {
	return annotation;
    }

    public void setAnnotation(String annotation) {
	this.annotation = annotation;
    }
    
    public static ReportData fromJsonObject(JsonObject o) {
	try {
	    Integer id = o.get("id").getAsInt();
	    String name = o.get("name").getAsString();
	    Integer period = o.get("period").getAsJsonObject().get("id").getAsInt();
	    String anno = o.get("annotation").getAsString();
	
	    return new ReportData(id, name, period, anno);
	} catch(Exception e) {
	    return null;
	}
	
    }

    
}
