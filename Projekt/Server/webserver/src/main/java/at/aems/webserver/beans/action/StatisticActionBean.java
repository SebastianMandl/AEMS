/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.action;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsDeleteAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUpdateAction;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.beans.AbstractBean;
import java.net.HttpURLConnection;
import javax.faces.bean.ManagedBean;

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
        AemsUpdateAction update = new AemsUpdateAction(userBean.getAemsUser(), EncryptionType.SSL);
	update.setTable("Statistics");
	update.setIdColumn("id", statisticId);
	update.write("display_android", true);
	try {
	    configureApiParams();
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
	update.write("display_android", true);
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
        AemsDeleteAction delete = new AemsDeleteAction(userBean.getAemsUser(), EncryptionType.SSL);
	delete.setTable("Statistics");
	delete.setIdColumn("id", statisticId);
	try {
	    configureApiParams();
	    AemsAPI.call0(delete, null);
	    notify.setMessage("Die Statistik wurde entfernt!");
	} catch(Exception ex) {
	    notify.setMessage("Ein Fehler ist aufgetreten!");
	}
	callUpdateOn("statisticBean");
	
        return "einstellungenStatistiken";
    }
}
