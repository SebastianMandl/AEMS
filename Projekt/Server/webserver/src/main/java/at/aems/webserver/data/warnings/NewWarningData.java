/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.warnings;

import at.aems.webserver.data.AemsApiData;
import at.aems.webserver.data.ApiAction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Niggi
 */
public class NewWarningData extends AemsApiData {
    
    private String name = "";
    private WarningType type = WarningType.BENACHRICHTIGUNG;
    private List<String> meterIds = new ArrayList<String>();
    private int variance = 0;
    private List<String> exceptionDays = new ArrayList<String>(); 
    private List<String> exceptionDates = new ArrayList<String>();
    
    public NewWarningData(int userId, String authString) {
        super(userId, authString, ApiAction.CREATE_NOTIFICATION);
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

    public List<String> getMeterIds() {
        return meterIds;
    }

    public void setMeterIds(List<String> meterIds) {
        this.meterIds = meterIds;
    }

    public int getVariance() {
        return variance;
    }

    public void setVariance(int variance) {
        this.variance = variance;
    }

    public List<String> getExceptionDays() {
        return exceptionDays;
    }

    public void setExceptionDays(List<String> exceptionDays) {
        this.exceptionDays = exceptionDays;
    }

    public List<String> getExceptionDates() {
        return exceptionDates;
    }

    public void setExceptionDates(List<String> exceptionDates) {
        this.exceptionDates = exceptionDates;
    }
    
    
}
