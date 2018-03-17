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
import com.google.gson.JsonObject;
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
	if(userBean == null)
	    return;
	 
	AemsQueryAction qry = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
	qry.setQuery(AemsUtils.getQuery("created_statistics", NewMap.of("USER", userBean.getUserId())));
	
	JsonArray result = null;
	try {
	    AemsAPI.setUrl(AemsUtils.API_URL);
	    AemsResponse response = AemsAPI.call0(qry, null);
	    result = response.getJsonArrayWithinObject();
	} catch (IOException | IllegalStateException ex) {
	    Logger.getLogger(UserStatisticsBean.class.getName()).log(Level.SEVERE, null, ex);
	    errorBean.setError("Anscheinend gibt es gerade Probleme mit der API!");
	}
	
	if(result == null)
	    return;
	
	for(JsonElement e : result) {
	    JsonObject current = e.getAsJsonObject();
	    
	    StatisticMeta meta = new StatisticMeta(current.get("id").getAsInt(), current.get("name").getAsString());
	    meta.setAndroid(true);
	    meta.setStartpage(true);
	    meta.setAnnotation("This is a statistic from the database! Hooray!");
	    
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

}
