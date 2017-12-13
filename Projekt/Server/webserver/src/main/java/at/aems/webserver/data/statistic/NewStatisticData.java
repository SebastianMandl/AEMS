/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.statistic;

import at.aems.webserver.data.AemsApiData;
import at.aems.webserver.data.ApiAction;

/**
 *
 * @author Niklas
 */
public class NewStatisticData extends AemsApiData {
    
    private String statisticName;
    private String[] meters;
    private Period period;
    private String annotation;
    
    public NewStatisticData(int userId, String authString) {
        super(userId, authString, ApiAction.CREATE_STATISTIC);
    }

    public String getStatisticName() {
        return statisticName;
    }

    public void setStatisticName(String statisticName) {
        this.statisticName = statisticName;
    }

    public String[] getMeters() {
        return meters;
    }

    public void setMeters(String[] meters) {
        this.meters = meters;
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
    
    
    
}
