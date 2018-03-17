/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.display;

import at.aems.accumulator.TimePeriod;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.NewMap;
import at.aems.webserver.data.statistic.DisplayedStatistic;
import at.aems.webserver.data.statistic.Period;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class StatisticDisplayBean extends AbstractDisplayBean {

    private List<DisplayedStatistic> statistics;

    public StatisticDisplayBean() {
    }


    @Override
    public void update() {
	configureApiParams();
        statistics = new ArrayList<>();
	
	List<DisplayedStatistic> statisticIds = getStatisticsOfUser();
	if(statisticIds == null) {
	    return;
	}
	
	Map<DisplayedStatistic, List<String>> statisticMeters =
		getStatisticMeters(statisticIds);
	
	fillStatisticValues(statisticMeters);
	
	for(DisplayedStatistic stat : statisticIds) {
	    statistics.add(stat);
	}


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
        for (int i = 0; i < amount; i++) {
            l.add(r.nextInt(200) + 100);
        }
        return l;
    }

    private List<DisplayedStatistic> getStatisticsOfUser() {
	List<DisplayedStatistic> result = new ArrayList<>();
	try {
	    AemsQueryAction qry = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
	    qry.setQuery(AemsUtils.getQuery("homepage_statistics", NewMap.of("USER", userBean.getUserId())));
	    AemsResponse resp = AemsAPI.call0(qry, null);
	    System.out.println(resp.getDecryptedResponse());
	    JsonArray array = resp.getJsonArrayWithinObject();
	    for(JsonElement element : array) {
		if(!element.isJsonObject())
		    continue;
		JsonObject obj = element.getAsJsonObject();
		
		Integer id = obj.get("id").getAsInt();
		String name = obj.get("name").getAsString();
		Period period = obj.has("period") ? Period.byId(obj.get("period").getAsInt()) : Period.WEEKLY;
		
		String displayHome;
		if(!obj.has("display_home")) {
		    displayHome = "true";
		} else {
		    displayHome = obj.get("display_home").getAsString();
		}

		if(Boolean.parseBoolean(displayHome)) {
		    result.add(new DisplayedStatistic(id, name, period));
		}
	    }
	} catch(Exception ex) {
	    Logger.getLogger(StatisticDisplayBean.class.getName()).log(Level.SEVERE, null, ex);
	    return null;
	}
	
	return result;
	
    }

    private Map<DisplayedStatistic, List<String>> getStatisticMeters(List<DisplayedStatistic> statisticIds) {
	Map<DisplayedStatistic, List<String>> result = new HashMap<>();
	try {
	    
	    for(DisplayedStatistic statistic : statisticIds) {
		result.put(statistic, new ArrayList<String>());
		AemsQueryAction qry = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
		qry.setQuery(AemsUtils.getQuery("statistic_meters", 
			NewMap.of("STATISTIC_ID", statistic.getId())));
		
		AemsResponse resp = AemsAPI.call0(qry, null);
		JsonArray array = resp.getJsonArrayWithinObject();
		
		for(JsonElement element : array) {
		    String id = element.getAsJsonObject().get("meter")
			    .getAsJsonObject().get("id").getAsString();
		    result.get(statistic).add(id);
		}
		
	    }
	    
	} catch(Exception ex) {
	    return null;
	}
	
	return result;
    }

    private void fillStatisticValues(Map<DisplayedStatistic, List<String>> statisticMeters) {
	
	for(Map.Entry<DisplayedStatistic, List<String>> entry : statisticMeters.entrySet()) {

	    TimePeriod period = entry.getKey().getPeriod().toTimePeriod();
	    Double[] accumulatedValues = new Double[period.getValues()];
	    
	    for(String meter : entry.getValue()) {
		AemsQueryAction qry = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
		qry.setQuery(AemsUtils.getQuery("statistic_values", 
			NewMap.of("METER_ID", meter,
				  "START", getStart(entry.getKey().getPeriod()))));
		
		NavigableMap<Timestamp, Double> values = new TreeMap<>();
		
		try {
		    AemsResponse r = AemsAPI.call0(qry, null);
		    JsonArray array = r.getJsonArrayWithinObject();
		    
		    for(JsonElement ele : array) {
			if(!ele.isJsonObject())
			    continue;
			JsonObject obj = ele.getAsJsonObject();
			
			Timestamp t = Timestamp.valueOf(obj.get("timestamp").getAsString());
			Double doub = obj.get("measured_value").getAsDouble();
			
			values.put(t, doub);
		    }
		    Double[] pre = period.accumulate(values);
		    for(int i = 0; i < accumulatedValues.length; i++) {
			if(pre[i] == null) {
			    break;
			}
			if(accumulatedValues[i] == null) {
			    accumulatedValues[i] = 0.0;
			}
			accumulatedValues[i] += pre[i];
		    }
		    
		} catch(IOException ex) {
		    
		}
	    }
	    
	    entry.getKey().setElectricityValues(Arrays.asList(accumulatedValues));
	    
	}
	
    }
    
    private String getStart(Period p) {
	GregorianCalendar calendar = new GregorianCalendar();
	int daysToSubtract = -1;
	switch(p) {
	    case DAILY:
		daysToSubtract = 2;
		break;
	    case WEEKLY:
		daysToSubtract = 8;
		break;
	    case MONTHLY:
		daysToSubtract = 32;
		break;
	    case YEARLY:
		daysToSubtract = 365;
		break;
	}
	
	calendar.add(Calendar.DAY_OF_YEAR, -daysToSubtract);
	
	return new Timestamp(calendar.getTimeInMillis()).toString();
    }

}
