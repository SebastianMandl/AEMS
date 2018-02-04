/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.action;

import at.aems.webserver.beans.AbstractBean;
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
        notify.setMessage("OK" + statisticId);
        return "einstellungenStatistiken";
    }

    public String removeFromIndex(Integer statisticId) {
        notify.setMessage("OK" + statisticId);
        return "einstellungenStatistiken";
    }

    public String addToAndroid(Integer statisticId) {
        notify.setMessage("Statistik wurde zur Android-App hinzugefügt");
        return "einstellungenStatistiken";
    }

    public String addToIndex(Integer statisticId) {
        notify.setMessage("Statistik wurde zur Startseite hinzugefügt");
        return "einstellungenStatistiken";
    }

    public String remove(Integer statisticId) {
        notify.setMessage("Statistik wurde gelöscht");
        return "einstellungenStatistiken";
    }
}
