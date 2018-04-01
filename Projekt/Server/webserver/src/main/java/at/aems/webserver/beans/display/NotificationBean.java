/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.display;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.NewMap;
import at.aems.webserver.data.notifications.SimpleNotificationData;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
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

    @Override
    public void update() {
        
        notifications = new ArrayList<>();
	if(userBean == null || !notifications.isEmpty())
	    return;
	
	configureApiParams();
        AemsQueryAction notificationQuery = new AemsQueryAction(userBean.getAemsUser());
	notificationQuery.setQuery(AemsUtils.getQuery("notices", NewMap.of()));
	
	try {
	    AemsResponse response = AemsAPI.call0(notificationQuery, null);
	    if(!response.isOk()) {
		return;
	    }
	    
	    StackTraceElement[] s = Thread.currentThread().getStackTrace();
	    JsonArray notices = response.getJsonArrayWithinObject();
	    System.out.println(" ---- update --- " + hashCode());
	    for(JsonElement e : notices) {
		SimpleNotificationData data = SimpleNotificationData.fromJsonObject(e.getAsJsonObject());
		if(data != null && !data.wasSeen()) {
		    notifications.add(data); 
		}
	    }
	    
	} catch(Exception ex) {
	    Logger.getLogger(NotificationBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    } 
    
    private JsonArray getJsonData(AemsQueryAction query) {
        try {
            AemsAPI.setUrl(AemsUtils.API_URL);
            AemsResponse data = AemsAPI.call0(query, null);
            return data.getJsonArrayWithinObject();
        } catch (Exception ex) {
            Logger.getLogger(NotificationBean.class.getName()).log(Level.SEVERE, null, "Cannot fetch notifications!");
        }
        return new JsonArray();
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

    @Override
    public int hashCode() {
	return (int) System.currentTimeMillis() + super.hashCode();
    }

    
    
    
}
