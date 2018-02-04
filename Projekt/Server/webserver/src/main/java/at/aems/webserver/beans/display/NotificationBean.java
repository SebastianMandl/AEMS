/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.display;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.NewMap;
import at.aems.webserver.data.notifications.NotificationType;
import at.aems.webserver.data.notifications.SimpleNotificationData;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 * This managed bean stores notification data. Due to the fact that the
 * noficiation icon is present on every page, this bean is session scoped. It
 * will periodically fetch updates from the api
 *
 * @author Niklas
 */
@ManagedBean
@SessionScoped
public class NotificationBean extends AbstractDisplayBean {

    private List<SimpleNotificationData> notifications;
    private long lastUpdate;
    
    @ManagedProperty(value="#{userMeterBean}")
    private UserMeterBean userMeterBean;

    public NotificationBean() {
    }

    @Override
    public void update() {
        
        notifications = new ArrayList<>();
        AemsQueryAction notificationQuery = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
        
        for(Map.Entry<String, String> meter : userMeterBean.getMeters().entrySet()) {
            String meterId = meter.getKey();
            String query = AemsUtils.getQuery("archived_notifications", NewMap.of("METER_ID", meterId));
            notificationQuery.setQuery(query);
            String data = getRawData(notificationQuery);
            System.out.println("RAW: " + data);
            JsonArray array = new JsonParser().parse(data).getAsJsonArray();
            for(int i = 0; i < array.size(); i++) {
                JsonObject current = array.get(i).getAsJsonObject();
                notifications.add(SimpleNotificationData.fromJsonObject(current));
            }
        }
        System.out.print("I HAVE " + getNotificationCount() + " notifications!");
        /*
        notifications = new ArrayList<>();
        notifications.add(new SimpleNotificationData(123, "Schreckliches ist passiert!", NotificationType.WARNING));
        */
    }
    
    private String getRawData(AemsQueryAction query) {
        try {
            AemsAPI.setUrl(AemsUtils.API_URL + "/warnings.txt");
            String data = AemsAPI.call(query, new byte[16]);
            return new String(Base64.getUrlDecoder().decode(data));
        } catch (IOException ex) {
            Logger.getLogger(NotificationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private boolean needsUpdate() {
        // TODO: If last update was like 5 minutes ago then return true
        return true;
    }

    public void setUserMeterBean(UserMeterBean userMeterBean) {
        this.userMeterBean = userMeterBean;
    }

    
    public int getNotificationCount() {
        return notifications != null ? notifications.size() : -1;
    }
    
    public String getNofificationsAsJson() {
        return new Gson().toJson(notifications);
    }

    /**
     * @deprecated
     */
    private String getNotificationQueryString() {
        return String.join("\n",
                "{",
                    "archived_meter_notifications(user: " + userBean.getUserId() + ") {",
                        "id",
                        "meter",
                        "notification {",
                            "type",
                        "}",
                    "}",
                "}");
    }

    
    
}
