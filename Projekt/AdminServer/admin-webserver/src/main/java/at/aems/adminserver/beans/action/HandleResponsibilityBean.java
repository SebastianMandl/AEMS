/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.action;

import at.aems.adminserver.Constants;
import at.aems.adminserver.beans.display.ResponsibilityBean;
import at.aems.adminserver.data.users.Responsibility;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsDeleteAction;
import at.aems.apilib.AemsInsertAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.crypto.EncryptionType;
import java.util.List;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class HandleResponsibilityBean extends AbstractActionBean {
    private String postalCode;
    private String placeName;

    public HandleResponsibilityBean() {
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String doAdd() {
        System.out.println(postalCode + " " + placeName);
        // Check if user already has responsibility with that postal code
        List<Responsibility> resp = ((ResponsibilityBean)getDisplayBean("responsibilityBean")).getResponsibilities();
        for(Responsibility r : resp) {
            if(r.getPostalCode().equals(postalCode)) {
                notify.setMessage("Dieser Zuständigkeitsbereich existiert bereits");
                return "zustaendigkeit";
            }
        }
	
	AemsInsertAction in = new AemsInsertAction(userBean.getAemsUser(), EncryptionType.SSL);
	in.setTable("Responsibilities");
	in.write("user", userBean.getUserId()); 
	in.write("postal_code", postalCode);
	in.write("designation", placeName);
	in.endWrite();
	
	try {
	    AemsAPI.setUrl(Constants.API_URL);
	    AemsResponse r = AemsAPI.call0(in, null);
	    notify.setMessage("Zuständigkeitsbereich wurde hinzugefügt!");
	    callUpdateOn("responsibilityBean");
	} catch(Exception ex) {
	    notify.setMessage("Es ist ein Fehler aufgetreten!");
	}
  
        
        return "zustaendigkeit";
    }
    
    public String doDelete() {
        
	AemsDeleteAction del = new AemsDeleteAction(userBean.getAemsUser(), EncryptionType.SSL);
	del.setTable("Responsibilities");
	del.setIdColumn("postal_code", postalCode);
	
	try {
	    setApiUrl();
	    AemsResponse resp = AemsAPI.call0(del, null);
	    if(resp.getResponseCode() == 200) {
		notify.setMessage("Zuständigkeit wurde erfolgreich entfernt!");
	    } else {
		notify.setMessage("Ein Fehler ist aufgetreten");
	    }
	} catch(Exception ex) {
	    notify.setMessage("Ein Fehler ist aufgetreten");
	}
	callUpdateOn("responsibilityBean");
        return "zustaendigkeit";
    }
    
    public String doAlter() {
        notify.setMessage("Zuständigkeitsbereich wurde bearbeitet!");
        return "zustaendigkeit";
    }
    
}
