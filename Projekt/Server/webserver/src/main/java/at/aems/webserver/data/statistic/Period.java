/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.statistic;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Niklas
 */
public enum Period {
    @SerializedName("0")
    DAILY(0, "Tag"), 
    
    @SerializedName("1")
    WEEKLY(1, "Woche"), 
    
    @SerializedName("2")
    MONTHLY(2, "Monat"), 
    
    @SerializedName("3")
    YEARLY(3, "Jahr");
    
    private int periodId;
    private String label;
    
    private Period(int id, String label) {
        this.periodId = id;
        this.label = label;
    }
    
    public int getPeriodId(){
        return this.periodId;
    }
    
    public String getLabel() {
        return label;
    }
}
