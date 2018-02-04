/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.display;

import at.aems.webserver.data.statistic.StatisticData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public class UserStatisticsBean extends AbstractDisplayBean {
    private List<String> categories = new ArrayList<>();
    private Map<Integer, StatisticData> statistics = new HashMap<>();

    public UserStatisticsBean() {
    }

    @Override
    public void update() {
        statistics.put(123, new StatisticData("Strom", "Stromstatistik 1"));
        statistics.put(124, new StatisticData("Strom", "Stromstatistik X"));
        statistics.put(125, new StatisticData("Gas", "Andere Statistik 1"));

        categories.add("Strom");
        categories.add("Gas");      
    }
    

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public Map<Integer, StatisticData> getStatistics() {
        return statistics;
    }

    public void setStatistics(Map<Integer, StatisticData> statistics) {
        this.statistics = statistics;
    }
 
    
}