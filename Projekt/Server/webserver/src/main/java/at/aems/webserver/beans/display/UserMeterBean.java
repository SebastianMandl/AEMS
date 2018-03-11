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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
public class UserMeterBean extends AbstractDisplayBean {

    private Set<String> meterTypes;
    private Map<String, String> meters;

    @Override
    public void update() {
	configureApiParams();
	// Initialize to empty
	meterTypes = new HashSet<>();
	meters = new HashMap<>();

	// retrieve GraphQL-Query from text file and send to REST
	AemsQueryAction query = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
	query.setQuery(AemsUtils.getQuery("meters_simple", NewMap.of("USER_ID", userBean.getUserId())));
	JsonArray data = getJsonResponse(query);

	if (data == null) {
	    return;
	}
	
	System.out.println(data);

	try {
	    for (int i = 0; i < data.size(); i++) {
		JsonObject j = data.get(i).getAsJsonObject();
		String id = j.get("id").getAsString();
		JsonObject metertype = j.get("metertype").getAsJsonObject();
		String type = metertype.has("name") ? metertype.get("name").getAsString() : "N/A";
		meters.put(id, type);
	    }
	} catch(Exception ex) {
	    // yes
	}

	// Since meterTypes is a HashSet, duplicate values will be erased
	meterTypes.addAll(meters.values());

    }

    public Set<String> getMeterTypes() {
	return meterTypes;
    }

    public void setMeterTypes(Set<String> meterTypes) {
	this.meterTypes = meterTypes;
    }

    public Map<String, String> getMeters() {
	return meters;
    }

    public void setMeters(Map<String, String> meters) {
	this.meters = meters;
    }

    private JsonArray getJsonResponse(AemsQueryAction query) {
	AemsResponse resp = null;
	try {
	    AemsAPI.setUrl(AemsUtils.API_URL);
	    resp = AemsAPI.call0(query, null);
	    JsonArray arr = resp.getJsonArrayWithinObject();
	    for (int i = 0; i < arr.size(); i++) {
		if (arr.get(i).isJsonObject() && arr.get(i).getAsJsonObject().keySet().isEmpty()) {
		    arr.remove(i);
		}
	    }
	    return arr;
	} catch (IOException | IllegalStateException ex) {
	    Logger.getLogger(UserMeterBean.class.getName()).log(Level.SEVERE, "OH FUCC");
	    Logger.getLogger(UserMeterBean.class.getName()).log(Level.SEVERE, null, ex);
	}
	return null;
    }

}
