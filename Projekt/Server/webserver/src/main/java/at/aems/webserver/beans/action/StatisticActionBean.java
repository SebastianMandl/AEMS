/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.action;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsDeleteAction;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUpdateAction;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.NewMap;
import at.aems.webserver.beans.AbstractBean;
import com.google.gson.JsonArray;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import javax.faces.bean.ManagedBean;
import org.ocpsoft.logging.Logger;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class StatisticActionBean extends AbstractActionBean {

    public static final String SECTION_ANDROID = "Android-App";
    public static final String SECTION_INDEX = "Startseite";
    
    public String removeFromAndroid(Integer statisticId) {
        AemsUpdateAction update = new AemsUpdateAction(userBean.getAemsUser(), EncryptionType.SSL);
	update.setTable("Statistics");
	update.setIdColumn("id", statisticId);
	update.write("display_android", false);
	try {
	    configureApiParams();
	    AemsAPI.call0(update, null);
	    notify.setMessage("Statistik aus Android-App entfernt!");
	} catch(Exception ex) {
	    notify.setMessage("Ein Fehler ist aufgetreten!");
	}
	callUpdateOn("statisticBean");
        return "einstellungenStatistiken";
    }

    public String removeFromIndex(Integer statisticId) {
        AemsUpdateAction update = new AemsUpdateAction(userBean.getAemsUser(), EncryptionType.SSL);
	update.setTable("Statistics");
	update.setIdColumn("id", statisticId);
	update.write("display_home", false);
	try {
	    configureApiParams();
	    AemsAPI.call0(update, null);
	    notify.setMessage("Statistik aus Startseite entfernt!");
	} catch(Exception ex) {
	    notify.setMessage("Ein Fehler ist aufgetreten!");
	}
	callUpdateOn("statisticBean");
        return "einstellungenStatistiken";
    }

    public String addToAndroid(Integer statisticId) {
	
	configureApiParams();
	AemsQueryAction qry = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
	qry.setQuery(AemsUtils.getQuery("android_statistics", NewMap.of("USER", userBean.getUserId())));
	try {
	    AemsResponse resp = AemsAPI.call0(qry, null);
	    JsonArray array = resp.getJsonArrayWithinObject();
	    System.out.println(resp.getDecryptedResponse());
	    System.out.println("Array Size: " + array.size());
	    if(array.size() >= 3) {
		notify.setMessage("Maximal 3 Android Statistiken möglich!");
		return "einstellungenStatistiken";
	    }
	} catch(Exception ex) {
	    return "einstellungenStatistiken";
	}
	
        AemsUpdateAction update = new AemsUpdateAction(userBean.getAemsUser(), EncryptionType.SSL);
	update.setTable("Statistics");
	update.setIdColumn("id", statisticId);
	update.write("display_android", true);
	try {
	    AemsAPI.call0(update, null);
	    System.out.println(update.toJsonObject());
	    notify.setMessage("Statistik zu Android-App hinzugefügt!");
	} catch(Exception ex) {
	    notify.setMessage("Ein Fehler ist aufgetreten!");
	}
	callUpdateOn("statisticBean");
        return "einstellungenStatistiken";
    }

    public String addToIndex(Integer statisticId) {
        AemsUpdateAction update = new AemsUpdateAction(userBean.getAemsUser(), EncryptionType.SSL);
	update.setTable("Statistics");
	update.setIdColumn("id", statisticId);
	update.write("display_home", true);
	try {
	    configureApiParams();
	    AemsAPI.call0(update, null);
	    notify.setMessage("Statistik zu Startseite hinzugefügt!");
	} catch(Exception ex) {
	    notify.setMessage("Ein Fehler ist aufgetreten!");
	}
	callUpdateOn("statisticBean");
        return "einstellungenStatistiken";
    }

    public String remove(Integer statisticId) {
	
	configureApiParams();
	AemsDeleteAction deleteMeters = new AemsDeleteAction(userBean.getAemsUser(), EncryptionType.SSL);
	deleteMeters.setTable("statistic_meters");
	deleteMeters.setIdColumn("statistic", statisticId);
	
	try {
	    AemsAPI.call0(deleteMeters, null);
	} catch(Exception ex) {
	    // yes
	}
	
        AemsDeleteAction delete = new AemsDeleteAction(userBean.getAemsUser(), EncryptionType.SSL);
	delete.setTable("Statistics");
	delete.setIdColumn("id", statisticId);
	try {
	    
	    AemsAPI.call0(delete, null);
	    notify.setMessage("Die Statistik wurde entfernt!");
	} catch(Exception ex) {
	    notify.setMessage("Ein Fehler ist aufgetreten!");
	    java.util.logging.Logger.getLogger("StatisticActionBean").log(Level.SEVERE, null, ex);
	}
	callUpdateOn("statisticBean");
	
        return "einstellungenStatistiken";
    }
}
