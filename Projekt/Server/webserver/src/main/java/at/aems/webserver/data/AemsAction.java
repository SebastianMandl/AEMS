/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Niggi
 */
public class AemsAction {
	private int userId;
	private String authString;
	private String action;
	
	private transient Map<String, Map<String, Object>> apiData;
	
	public AemsAction(int user, String auth, String action) {
		this.userId = user;
		this.authString = auth;
		this.action = action;
		this.apiData = new HashMap<>();
	}
	
	public void addTable(String tableName) {
		apiData.put(tableName, new HashMap<String, Object>());
	}
	
	public Map<String, Object> getTable(String tableName) {
		return apiData.get(tableName);
	}
	
	public String toJson() {
		
		GsonBuilder builder = new GsonBuilder()
				.setPrettyPrinting()
				.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
		
		JsonObject root = (JsonObject) builder.create().toJsonTree(this);
		JsonObject data = new JsonObject();
		for(Entry<String, Map<String, Object>> entry : apiData.entrySet()) {
			data.add(entry.getKey(), builder.create().toJsonTree(entry.getValue()));
		}
		
		root.add("data", data);
		
		return builder.create().toJson(root);
		
		
	}
}