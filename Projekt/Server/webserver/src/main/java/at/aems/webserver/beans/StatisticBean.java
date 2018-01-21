/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.webserver.AemsUtils;
import at.aems.webserver.data.statistic.Anomaly;
import at.aems.webserver.data.statistic.Period;
import at.aems.webserver.data.statistic.StatisticMeta;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class StatisticBean {

    private List<StatisticMeta> allStatistics = new ArrayList<>();

    public StatisticBean() {

    }

    @PostConstruct
    public void init() {
        StatisticMeta s = new StatisticMeta(100, "Meins Statistik!");
        s.setAnnotation("Hallo");
        s.setAnomalies(Arrays.asList(new Anomaly(10, "Temperatur", "Celsius")));
        s.setAndroid(true);
        s.setMeters(Arrays.asList("AT0001", "AT0002"));
        s.setPeriod(Period.DAILY);
        allStatistics.add(s);
    }

    public List<StatisticMeta> getStatistics() {
        return allStatistics;
    }

    public void setStatistics(List<StatisticMeta> statistics) {
        this.allStatistics = statistics;
    }

    public List<StatisticMeta> getAndroidStatistics() {
        final List<StatisticMeta> result = new ArrayList<>();
        allStatistics.forEach(new Consumer<StatisticMeta>() {
            @Override
            public void accept(StatisticMeta t) {
                if (t.isAndroid()) {
                    result.add(t);
                }
            }
        });
        return result;
    }

    public List<StatisticMeta> getStartpageStatistics() {
        final List<StatisticMeta> result = new ArrayList<>();
        allStatistics.forEach(new Consumer<StatisticMeta>() {
            @Override
            public void accept(StatisticMeta t) {
                if (t.isStartpage()) {
                    result.add(t);
                }
            }
        });
        return result;
    }

}
