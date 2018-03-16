/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.display;

import at.aems.adminserver.UserRole;
import at.aems.adminserver.data.users.DisplayedUser;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsRegisterAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.crypto.EncryptionType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public class AdminDisplayBean extends AbstractDisplayBean {
    
    private List<DisplayedUser> admins;
    private List<DisplayedUser> subAdmins;

    @Override
    public void update() { 
	admins = getUsersWithRole(UserRole.ADMIN);
	subAdmins = getUsersWithRole(UserRole.SUB_ADMIN);        
    }

    public List<DisplayedUser> getAdmins() {
        return admins;
    }

    public void setAdmins(List<DisplayedUser> admins) {
        this.admins = admins;
    }

    public List<DisplayedUser> getSubAdmins() {
        return subAdmins;
    }

    public void setSubAdmins(List<DisplayedUser> subAdmins) {
        this.subAdmins = subAdmins;
    }

    private List<DisplayedUser> getUsersWithRole(UserRole userRole) {
	List<DisplayedUser> result = new ArrayList<>();
	
	AemsQueryAction qry = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
	qry.setQuery("{ users(role:\"" + userRole.getId() + "\") { id username email postal_code }}");
	
	try {
	    AemsResponse response = AemsAPI.call0(qry, null);
	    JsonArray array = response.getJsonArrayWithinObject();
	    
	    for(JsonElement ele : array) {
		DisplayedUser user = DisplayedUser.fromJsonObject(ele.getAsJsonObject());
		if(user != null && user.getId() != null && user.getId() != userBean.getUserId()) {
		    result.add(user);
		}
	    }
	} catch(Exception ex) {
	    Logger.getLogger(AdminDisplayBean.class.getName()).log(Level.SEVERE, ex.getMessage());
	}
	
	return result;
    }
}
