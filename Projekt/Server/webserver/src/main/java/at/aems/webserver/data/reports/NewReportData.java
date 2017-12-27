/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.reports;

import at.aems.webserver.data.AemsApiData;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Niggi
 */
public class NewReportData extends AemsApiData {
    private String name = "";
    private int timePeriod;
    private List<String> statisticIds = new ArrayList<>();
    private boolean autoGenerate;
    private String annotation;

    public NewReportData(int userId, String authString) {
        super(userId, authString, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(int timePeriod) {
        this.timePeriod = timePeriod;
    }

    public List<String> getStatisticIds() {
        return statisticIds;
    }

    public void setStatisticIds(List<String> statisticIds) {
        this.statisticIds = statisticIds;
    }

    public boolean isAutoGenerate() {
        return autoGenerate;
    }

    public void setAutoGenerate(boolean autoGenerate) {
        this.autoGenerate = autoGenerate;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }
    
    
    
    
    
    
    
}
