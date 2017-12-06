/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import java.lang.reflect.Field;

/**
 *
 * @author Niklas
 */
public abstract class AemsApiData {
    // Transient in order to keep GSON from serializing these attributes
    private transient int userId;
    private transient String authString;
    private transient ApiAction action;
    
    public AemsApiData(int userId, String authString, ApiAction action) {
        this.userId = userId;
        this.authString = authString;
        this.action = action;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAuthString() {
        return authString;
    }

    public void setAuthString(String authString) {
        this.authString = authString;
    }
    
    public ApiAction getAction() {
        return action;
    }
    
    public String toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("user_id", getUserId());
        obj.addProperty("auth_string", getAuthString());
        obj.addProperty("action", getAction().name());
        
        GsonBuilder builder = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        JsonObject data = (JsonObject) builder.create().toJsonTree(this);
        obj.add("data", data);
        
        return new GsonBuilder().setPrettyPrinting().create().toJson(obj);
    }
    
}
