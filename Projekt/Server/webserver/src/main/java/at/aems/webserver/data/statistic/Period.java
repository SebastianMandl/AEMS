/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.statistic;

import at.aems.accumulator.TimePeriod;
import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.IsoFields;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author Niklas
 */
public enum Period {
    @SerializedName("1")
    DAILY(1, "Tag", new String[]{"0 - 3 Uhr", "3 - 6 Uhr", "6 - 9 Uhr", "9 - 12 Uhr",
				 "12 - 15 Uhr", "15 - 18 Uhr", "18 - 21 Uhr", "21 - 24 Uhr"}), 
    //
    
    @SerializedName("2")
    WEEKLY(2, "Woche", new String[]{"Montag", "Dienstag","Mittwoch","Donnerstag","Freitag","Samstag","Sonntag"}),  //
    
    @SerializedName("3")
    MONTHLY(3, "Monat", new String[]{"getweeklabels"}),
    
    @SerializedName("4")
    YEARLY(4, "Jahr", new String[]{"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", 
                     "August", "September", "Oktober", "November", "Dezember"}); //
   
    
    private int periodId;
    private String label;
    private String[] labels;
    
    private Period(int id, String label, String[]labels) {
        this.periodId = id;
        this.label = label;
        this.labels = labels;
    }
    
    public static Period byId(int id) {
        for(Period p : Period.values()) {
            if(p.getPeriodId() == id) {
                return p;
            }
        }
        return null;
    }
    
    public int getPeriodId(){
        return this.periodId;
    }
    
    public String getLabel() {
       
        return label;
    }
    
    public String[] getLabels() {
        if(this == Period.MONTHLY) {
            return getWeekLabels();
        }
        return labels;
    }
    
    public TimePeriod toTimePeriod() {
	switch(this) {
	    case DAILY:
		return TimePeriod.DAYLY;
	    case WEEKLY: 
		return TimePeriod.WEEKLY;
	    case MONTHLY:
		return TimePeriod.MONTHLY;
	    case YEARLY:
		return TimePeriod.YEARLY;
	}
	return null;
    }
    
    private static String[] getWeekLabels() {
        LocalDate localDate = LocalDate.now();
        int weekNumber = localDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        
	String[] weeks = new String[5];
	for(int i = 4; i >= 0; i--) {
	    weeks[i] = weekNumber < 10 ? "KW 0" + weekNumber : "KW " + weekNumber;
	    weekNumber--;
	}
	return weeks;
    } 
}
