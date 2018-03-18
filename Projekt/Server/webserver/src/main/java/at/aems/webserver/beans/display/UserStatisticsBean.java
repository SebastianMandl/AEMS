/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.display;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.data.statistic.StatisticData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
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

    @Override
    public void update() {
	
	AemsQueryAction qry = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
	qry.setQuery(AemsUtils.getQuery("created_statistics", new HashMap<String, String>()));
	
	JsonArray result = null;
	try {
	    configureApiParams();
	    AemsResponse response = AemsAPI.call0(qry, null);
	    result = response.getJsonArrayWithinObject();
	} catch (IOException | IllegalStateException ex) {
	    Logger.getLogger(UserStatisticsBean.class.getName()).log(Level.SEVERE, null, ex);
	}
	
	if(result == null)
	    return;
	
	for(JsonElement e : result) {
	    JsonObject current = e.getAsJsonObject();
	    
	    Integer id = current.get("id").getAsInt();
	    String name = current.get("name").getAsString();
	    String type = getStatisticType(id);
	    statistics.put(id, new StatisticData(type, name));
	}
	
	for(StatisticData sd : statistics.values()) {
	    if(!categories.contains(sd.getType())) {
		categories.add(sd.getType());
	    }
	}
	
    }
    
    private String getStatisticType(Integer id) {
	
	try {
	    
	    AemsQueryAction query1 = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
	    query1.setQuery("{ statistic_meters(statistic: \"" + id + "\") { meter { id metertype { name } } } }");
	    configureApiParams();
	    AemsResponse resp = AemsAPI.call0(query1, null);
	    
	    JsonArray nestedArray = resp.getJsonArrayWithinObject();
	    if(nestedArray.size() > 0) {
		String name = JsonPath.read(nestedArray.toString(), "$[0].meter.metertype.name");
		return name;
	    }
	    
	} catch(Exception ex) {
	    return "undefined";
	}
	
	return "undefined";
	
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