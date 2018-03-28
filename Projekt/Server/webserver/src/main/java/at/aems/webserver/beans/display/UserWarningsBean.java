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
import at.aems.webserver.data.warnings.AemsScript;
import at.aems.webserver.data.warnings.WarningData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sun.istack.internal.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public class UserWarningsBean extends AbstractDisplayBean {

    private List<WarningData> allWarnings = new ArrayList<>();

    @Override
    public void update() {
	allWarnings = new ArrayList<>();
	configureApiParams();

	AemsQueryAction anomalyQry = new AemsQueryAction(userBean.getAemsUser());
	anomalyQry.setQuery(AemsUtils.getQuery("anomalies", NewMap.of()));

	try {
	    AemsResponse resp = AemsAPI.call0(anomalyQry, null);
	    if (resp == null || resp.getResponseCode() != 200) {
		throw new RuntimeException("ys");
	    }
	    JsonArray anomalies = resp.getJsonArrayWithinObject();
	    for (JsonElement e : anomalies) {
		WarningData d = WarningData.fromJsonObject(e.getAsJsonObject());

		if (d != null) {
		    if (d.getName() == null) {
			d.setName(" - Kein Titel -");
		    }
		    this.allWarnings.add(d);
		}
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public List<WarningData> getAllNotifications() {
	return allWarnings;
    }

    public void setAllNotifications(List<WarningData> allWarnings) {
	this.allWarnings = allWarnings;
    }

    public List<WarningData> getWarnings() {
	final List<WarningData> result = new ArrayList<>();
	allWarnings.forEach(new Consumer<WarningData>() {
	    @Override
	    public void accept(WarningData t) {
		if (t.getType() != null && t.getType() == AemsScript.TYPE_WARNING) {
		    result.add(t);
		}
	    }
	});
	return result;
    }

    public List<WarningData> getNotifications() {
	final List<WarningData> result = new ArrayList<>();
	allWarnings.forEach(new Consumer<WarningData>() {
	    @Override
	    public void accept(WarningData t) {
		if (t.getType() != null && t.getType() == AemsScript.TYPE_NOTICE) {
		    result.add(t);
		}
	    }
	});
	return result;
    }

}
