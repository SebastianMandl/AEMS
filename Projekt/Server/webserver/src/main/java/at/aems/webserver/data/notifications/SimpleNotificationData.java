/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.notifications;

import com.google.gson.JsonObject;

/**
 * 
 * @author Niklas
 */
public class SimpleNotificationData {
    private int notificationId;
    private String name;
    private NotificationType type;
    
    public SimpleNotificationData(int id, String name, NotificationType type) {
        this.notificationId = id;
        this.name = name;
        this.type = type;
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

    public NotificationType getType() {
        return type;
    }
    
    public String getTypeString() {
        return type.name();
    }

    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public static SimpleNotificationData fromJsonObject(JsonObject object) {
        Integer id = object.get("id").getAsInt();
        JsonObject noti = object.get("notification").getAsJsonObject();
        String name = noti.get("name").getAsString();
        Integer type = noti.get("type").getAsInt();
        
        return new SimpleNotificationData(id, name, NotificationType.byId(type));
    }
    
}
