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
import at.aems.webserver.NewMap;
import at.aems.webserver.data.statistic.StatisticMeta;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public class StatisticBean extends AbstractDisplayBean {

    private List<StatisticMeta> allStatistics = new ArrayList<>();

    @Override
    public void update() {
	allStatistics = new ArrayList<>();
	if (userBean == null) {
	    return;
	}

	AemsQueryAction qry = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
	qry.setQuery(AemsUtils.getQuery("created_statistics", NewMap.of("USER", userBean.getUserId())));

	JsonArray result = null;
	try {
	    configureApiParams();
	    AemsResponse response = AemsAPI.call0(qry, null);
	    result = response.getJsonArrayWithinObject();
	} catch (IOException | IllegalStateException ex) {
	    Logger.getLogger(UserStatisticsBean.class.getName()).log(Level.SEVERE, null, ex);
	    errorBean.setError("Anscheinend gibt es gerade Probleme mit der API!");
	}

	if (result == null) {
	    return;
	}

	for (JsonElement e : result) {
	    JsonObject current = e.getAsJsonObject();

	    StatisticMeta meta = new StatisticMeta(current.get("id").getAsInt(), current.get("name").getAsString());
	    if (current.has("display_android") && current.get("display_android") != JsonNull.INSTANCE) {
		meta.setAndroid(current.get("display_android").getAsBoolean());
	    }
	    if (current.has("display_home") && current.get("display_home") != JsonNull.INSTANCE) {
		meta.setStartpage(current.get("display_home").getAsBoolean());
	    }
	    if (current.has("annotation") && !current.get("annotation").getAsString().equals("null")) {
		meta.setAnnotation(current.get("annotation").getAsString());
	    } else {
		meta.setAnnotation(" - ");
	    }

	    meta.setStatisticType(getStatisticType(meta.getId()));

	    allStatistics.add(meta);
	}

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

    private String getStatisticType(Integer id) {
	try {
	    AemsQueryAction query1 = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
	    query1.setQuery("{ statistic_meters(statistic: \"" + id + "\") { meter { id metertype { name } } } }");
	    configureApiParams();
	    AemsResponse resp = AemsAPI.call0(query1, null);

	    JsonArray nestedArray = resp.getJsonArrayWithinObject();
	    if (nestedArray.size() > 0) {
		String name = JsonPath.read(nestedArray.toString(), "$[0].meter.metertype.name");
		return name;
	    }
	} catch (Exception ex) {
	    return "undefined";
	}
	return "undefined";
    }
    
    public List<String> getStatisticTypes() {
	List<String> result = new ArrayList<>();
	for(StatisticMeta m : this.allStatistics) {
	    if(!result.contains(m.getStatisticType())) {
		result.add(m.getStatisticType());
	    }
	}
	return result;
    }

}
