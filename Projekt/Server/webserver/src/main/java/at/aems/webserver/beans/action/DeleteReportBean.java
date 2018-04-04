/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.action;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsDeleteAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.crypto.EncryptionType;
import java.io.IOException;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class DeleteReportBean extends AbstractActionBean {
    
    public String doDelete(Integer id) {
	
	configureApiParams();
	deleteFromResolutionTable("report_statistics", id);
	AemsDeleteAction delete = new AemsDeleteAction(userBean.getAemsUser(), EncryptionType.SSL);
	delete.setTable("Reports");
	delete.setIdColumn("id", id);
	
	try {
	    AemsResponse response = AemsAPI.call0(delete, null);
	    if(response.getResponseCode() == 200) {
		notify.setMessage("Bericht wurde gelöscht!");
		callUpdateOn("userReportBean");
	    } else {
		notify.setMessage("Da ist etwas schief gelaufen!");
	    }
	} catch(IOException e) {
	    notify.setMessage("Da ist etwas schief gelaufen!");
	}
	
	return "einstellungenBerichte";
    }
    
    private boolean deleteFromResolutionTable(String tableName, Integer reportId) {
	AemsDeleteAction deleteMeters = new AemsDeleteAction(userBean.getAemsUser(), EncryptionType.SSL);
	deleteMeters.setTable(tableName);
	deleteMeters.setIdColumn("report", reportId);
	try {
	    AemsResponse re = AemsAPI.call0(deleteMeters, null);
	    return re.isOk();
	} catch(Exception ex) {
	    return false;
	}
    }
}
