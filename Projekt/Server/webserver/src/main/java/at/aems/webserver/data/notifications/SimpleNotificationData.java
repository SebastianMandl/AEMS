/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.notifications;

import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;

/**
 * 
 * @author Niklas
 */
public class SimpleNotificationData {
    private int notificationId;
    private String name;
    private String notificationType;
    private boolean seen;
    
    public SimpleNotificationData(int id, String name, String type, boolean seen) {
        this.notificationId = id;
        this.name = name;
        this.notificationType = type;
	this.seen = seen;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String meterId) {
        this.name = meterId;
    }

    public String getType() {
        return notificationType;
    }
    
    public String getTypeString() {
        return notificationType;
    }

    public void setType(String type) {
        this.notificationType = type;
    }

    public boolean wasSeen() {
	return seen;
    }

    public void setSeen(boolean seen) {
	this.seen = seen;
    }
    
    public static SimpleNotificationData fromJsonObject(JsonObject object) {
        Integer id = JsonPath.read(object.toString(), "$.id");
	if(id == null) {
	    return null;
	}
        String name = object.get("title").getAsString();
        String type = JsonPath.read(object.toString(), "$.notificationtype.display_name");
	String seen = object.get("seen").getAsString();
        
        return new SimpleNotificationData(id, name, type, Boolean.parseBoolean(seen));
    }
    
}
