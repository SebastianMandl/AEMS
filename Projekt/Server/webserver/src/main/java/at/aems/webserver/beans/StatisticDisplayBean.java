/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.webserver.data.statistic.DisplayedStatistic;
import at.aems.webserver.data.statistic.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class StatisticDisplayBean {
    private List<DisplayedStatistic> statistics;
    
    public StatisticDisplayBean() {}
    
    @PostConstruct
    public void init() {
        statistics = new ArrayList<>();
        GregorianCalendar c = new GregorianCalendar();
        DisplayedStatistic yearStatistic = new DisplayedStatistic(123, "Jahresstatistik", Period.YEARLY);
        yearStatistic.setElectricityValues(randomInts(c.get(Calendar.MONTH) + 1));
        yearStatistic.setPreviousValues(randomInts(c.get(Calendar.MONTH) + 1));
       
        DisplayedStatistic monthStatistic = new DisplayedStatistic(234, "Monatsstatistik", Period.MONTHLY);
        monthStatistic.setElectricityValues(randomInts(c.get(Calendar.WEEK_OF_MONTH)));
        monthStatistic.setPreviousValues(randomInts(c.get(Calendar.WEEK_OF_MONTH)));
        Map<String, List<Integer>> data = new HashMap<>();
        data.put("Temperatur", Arrays.asList(30, 25, 22, 18));
        data.put("Lichtstärke", Arrays.asList(50, 100, 20, 18));
        monthStatistic.setAnomalyValues(data);
        

        
        DisplayedStatistic weeklyStatistic = new DisplayedStatistic(456, "Wochenstatistik", Period.WEEKLY);
        weeklyStatistic.setElectricityValues(randomInts(c.get(Calendar.DAY_OF_WEEK) - 1));
        weeklyStatistic.setPreviousValues(randomInts(c.get(Calendar.DAY_OF_WEEK) - 1)); 
        
        DisplayedStatistic dailyStatistic = new DisplayedStatistic(678, "Tagesstatistik", Period.DAILY);
        
        dailyStatistic.setElectricityValues(randomInts(c.get(Calendar.HOUR_OF_DAY))); 
        dailyStatistic.setPreviousValues(randomInts(c.get(Calendar.HOUR_OF_DAY)));
        
        statistics.add(yearStatistic);
        statistics.add(monthStatistic);
        statistics.add(weeklyStatistic);
        statistics.add(dailyStatistic); 
        
    }

    public List<DisplayedStatistic> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<DisplayedStatistic> statistics) {
        this.statistics = statistics;
    }

    private List<Integer> randomInts(int amount) {
        List<Integer> l = new ArrayList<>();
        Random r = new Random();
        for(int i = 0; i < amount; i++) {
            l.add(r.nextInt(200) + 100);
        }
        return l;
    }
    
}
