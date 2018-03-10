/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.display;

import at.aems.adminserver.Constants;
import at.aems.adminserver.beans.action.AbstractActionBean;
import at.aems.adminserver.data.users.Responsibility;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.crypto.EncryptionType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public class ResponsibilityBean extends AbstractDisplayBean {
    private List<Responsibility> responsibilities = new ArrayList<>();

    public ResponsibilityBean() {
    }
    
    public List<Responsibility> getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(List<Responsibility> responsibilities) {
        this.responsibilities = responsibilities;
    }

    @Override
    public void update() {
	responsibilities.clear();
	AemsAPI.setUrl(Constants.API_URL); 
	AemsQueryAction qry = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
	qry.setQuery("{ responsibilities(user: \"" + userBean.getUserId() + "\") { postal_code designation user { id } } }");
	
	try {
	    AemsResponse resp = AemsAPI.call0(qry, null);
	    JsonArray array = resp.getJsonArrayWithinObject();
	    
	    for(JsonElement e : array) {
		Responsibility r = Responsibility.fromJsonObject(e.getAsJsonObject());
		if(r != null) {
		    responsibilities.add(r);
		}
	    }
	} catch(Exception ex) {
	    this.responsibilities.clear();
	    this.responsibilities.add(new Responsibility("404", "Error!"));
	    ex.printStackTrace();
	}
	
	
	
    }
    
    
}
