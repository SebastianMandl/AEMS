/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.display;

import at.aems.adminserver.UserRole;
import at.aems.adminserver.beans.UserBean;
import at.aems.adminserver.data.users.DisplayedUser;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.crypto.EncryptionType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public class AcceptedUsersBean extends AbstractDisplayBean {

    private List<DisplayedUser> users = new ArrayList<>();

    public AcceptedUsersBean() {
    }


    public List<DisplayedUser> getUsers() {
        return users;
    }

    public void setUsers(List<DisplayedUser> users) {
        this.users = users;
    }
    
    @Override
    public void update() {
	users = new ArrayList<>();
	configureApiParams();
	
	AemsQueryAction qry = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
	qry.setQuery("{ users(role: \"" + UserRole.MEMBER.getId() + "\") { id username email postal_code}}");
	
	try {
	    AemsResponse r = AemsAPI.call0(qry, null);
	    List<String> myResp = getMyResponsibilities();
	    for(JsonElement ele : r.getJsonArrayWithinObject()) {
		DisplayedUser u = DisplayedUser.fromJsonObject(ele.getAsJsonObject());
		if(u != null && u.getPostalCode() != null && myResp.contains(u.getPostalCode())) {
		    users.add(u);
		}
	    }
	} catch(Exception ex) {
	    users.add(new DisplayedUser(1234, "Error", "has@occured", "1234"));
	}
    }
    
    private List<String> getMyResponsibilities() {
	
	List<String> result = new ArrayList<>();
	AemsQueryAction qry = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
	qry.setQuery("{responsibilities(user: \"" + userBean.getUserId() + "\") { postal_code user { id } } }");
	 
	try { 
	    AemsResponse r = AemsAPI.call0(qry, null);
	    for(JsonElement ele : r.getJsonArrayWithinObject()) {
		JsonObject o = ele.getAsJsonObject();
		if(o.has("postal_code")) {
		    result.add(o.get("postal_code").getAsString());
		}
	    }
	} catch(IOException ex) {
	    throw new RuntimeException(ex.getCause());
	}
	
	return result;
    }

}
