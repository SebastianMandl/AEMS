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
import at.aems.webserver.data.reports.ReportData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public class UserReportBean extends AbstractDisplayBean {

    private List<ReportData> reports;
    
    @Override
    public void update() {
	reports = new ArrayList<>();
	
	try {
	    AemsAPI.setUrl(AemsUtils.API_URL);
	    AemsQueryAction qry = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
	    qry.setQuery(AemsUtils.getQuery("created_reports", new HashMap<String, String>()));
	    
	    AemsResponse r = AemsAPI.call0(qry, null);
	    System.err.println(r.getDecryptedResponse());
	    JsonArray array = r.getJsonArrayWithinObject();
	    
	    for(JsonElement e : array) {
		ReportData d = ReportData.fromJsonObject(e.getAsJsonObject());
		reports.add(d);
	    }
	    
	} catch(Exception e) {
	    
	    e.printStackTrace();
	}
    }

    public List<ReportData> getReports() {
	return reports;
    }

    public void setReports(List<ReportData> reports) {
	this.reports = reports;
    }
    
    
    
}
