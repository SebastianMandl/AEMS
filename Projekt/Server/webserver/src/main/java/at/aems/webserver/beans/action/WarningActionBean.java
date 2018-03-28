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
import at.aems.webserver.beans.AbstractBean;
import java.io.IOException;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class WarningActionBean extends AbstractActionBean {
    
    public String doDelete(Integer id) {	
	AemsDeleteAction delete = new AemsDeleteAction(userBean.getAemsUser(), EncryptionType.SSL);
	delete.setTable("anomalies");
	delete.setIdColumn("id", id);
	try {
	    AemsAPI.call0(delete, null);
	    notify.setMessage("Benachrichtigung wurde entfernt!");
	    callUpdateOn("userWarningsBean");
	    callUpdateOn("webUIBean");
	} catch(IOException e) {
	    notify.setMessage("Ein Fehler ist aufgetreten!");
	}
        return "einstellungenWarnungen";
    }
    
}
