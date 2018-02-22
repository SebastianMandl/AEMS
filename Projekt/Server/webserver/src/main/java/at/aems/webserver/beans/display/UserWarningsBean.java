/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.display;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.data.statistic.StatisticMeta;
import at.aems.webserver.data.warnings.WarningMeta;
import at.aems.webserver.data.warnings.WarningType;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public class UserWarningsBean extends AbstractDisplayBean {

    private List<WarningMeta> allWarnings = new ArrayList<>();

    @Override
    public void update() {
	AemsQueryAction qry = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
	qry.setQuery(AemsUtils.getQuery("created_statistics", new HashMap<String, String>()));
	
	JsonArray result = null;
	try {
	    AemsAPI.setUrl(AemsUtils.API_URL);
	    AemsResponse response = AemsAPI.call0(qry, null);
	    result = response.getJsonArrayWithinObject();
	} catch (IOException | IllegalStateException ex) {
	    Logger.getLogger(UserStatisticsBean.class.getName()).log(Level.SEVERE, null, ex);
	}
	
	if(result == null)
	    return;
	
    }

    public List<WarningMeta> getAllNotifications() {
        return allWarnings;
    }

    public void setAllNotifications(List<WarningMeta> allWarnings) {
        this.allWarnings = allWarnings;
    }

    public List<WarningMeta> getWarnings() {
        final List<WarningMeta> result = new ArrayList<>();
        allWarnings.forEach(new Consumer<WarningMeta>() {
            @Override
            public void accept(WarningMeta t) {
                if (t.getType() == WarningType.WARNUNG) {
                    result.add(t);
                }
            }
        });
        return result;
    }

    public List<WarningMeta> getNotifications() {
        final List<WarningMeta> result = new ArrayList<>();
        allWarnings.forEach(new Consumer<WarningMeta>() {
            @Override
            public void accept(WarningMeta t) {
                if (t.getType() == WarningType.BENACHRICHTIGUNG) {
                    result.add(t);
                }
            }
        });
        return result;
    }

}
