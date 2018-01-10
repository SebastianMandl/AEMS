/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.webserver.AemsUtils;
import at.aems.webserver.data.notifications.NotificationType;
import at.aems.webserver.data.notifications.SimpleNotificationData;
import com.google.gson.Gson;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * This managed bean stores notification data. Due to the fact that the
 * noficiation icon is present on every page, this bean is session scoped. It
 * will periodically fetch updates from the api
 *
 * @author Niklas
 */
@ManagedBean
@SessionScoped
public class NotificationBean implements Serializable {

    private List<SimpleNotificationData> notifications;
    private long lastUpdate;
    
    @ManagedProperty(value="#{user}")
    private UserBean user;

    public NotificationBean() {
    }

    public void init() {
        System.out.println(" ------ " + FacesContext.getCurrentInstance().getViewRoot().getViewId());
        if (needsUpdate()) {
            new Thread() {
                public void run() {
                    updateNotifications();
                }
            }.start();
            lastUpdate = System.currentTimeMillis();
        }
    }
    // Must be provided for injection (i guess)
    public void setUser(UserBean bean) {
        this.user = bean;
    }

    private boolean needsUpdate() {
        // TODO: If last update was like 5 minutes ago then return true
        return true;
    }

    public int getNotificationCount() {
        return notifications != null ? notifications.size() : -1;
    }
    
    public String getNofificationsAsJson() {
        return new Gson().toJson(notifications);
    }

    private void updateNotifications() {
        try {
            // TODO: Make this ASYNC
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(NotificationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.notifications = new ArrayList<>();
        
        Map<String, Object> postParameters = new HashMap<>();
        postParameters.put("query", getNotificationQueryString());
        
        JsonArray notificationArray = (JsonArray) AemsUtils.call("warnings.json", postParameters);
        
        for(int i = 0; i < notificationArray.size(); i++) {
            JsonObject currentObject = notificationArray.getJsonObject(i);
            int id = currentObject.getInt("id");
            
            JsonObject nestedNotification = currentObject.getJsonObject("notification");
            String name = nestedNotification.getString("name");
            NotificationType type = NotificationType.byId(nestedNotification.getInt("type"));
            
            notifications.add(new SimpleNotificationData(id, name, type));
         }
        
    }
    
    private String getNotificationQueryString() {
        return String.join("\n",
                "{",
                    "archived_meter_notifications(user: " + user.getUserId() + ") {",
                        "id",
                        "meter",
                        "notification {",
                            "type",
                        "}",
                    "}",
                "}");
    }
}
