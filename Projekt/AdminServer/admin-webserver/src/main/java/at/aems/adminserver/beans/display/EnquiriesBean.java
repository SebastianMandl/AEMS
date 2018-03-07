/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.display;

import at.aems.adminserver.Constants;
import at.aems.adminserver.UserRole;
import at.aems.adminserver.beans.UserBean;
import at.aems.adminserver.data.users.Enquiry;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.crypto.EncryptionType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
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
public class EnquiriesBean extends AbstractDisplayBean {
    
    private List<Enquiry> enquiries = new ArrayList<>();
    private boolean first = true;
    private int newEnquiriesCount;
    
    public EnquiriesBean() {
    }

    public List<Enquiry> getEnquiries() {
        return enquiries;
    }

    public void setEnquiries(List<Enquiry> enquiries) {
        this.enquiries = enquiries;
    }

    @Override
    public void update() {
        enquiries.clear();
	AemsAPI.setUrl(Constants.API_URL);
	AemsQueryAction qry = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
	qry.setQuery("{ users { id username role { id } email member_since use_netzonline } }");
	
	JsonArray users = getEnquiries(qry);
	for(JsonElement e : users) {
	    enquiries.add(Enquiry.fromJsonObject(e.getAsJsonObject()));
	}
	
        System.out.println(" ------ Update called on " + this.getClass().getSimpleName());
	/*
        Enquiry e = new Enquiry("graf@graf.graf", "Graf", true, new Timestamp(System.currentTimeMillis() - 1500000));
        Enquiry e2 = new Enquiry("knoll@knolli.k", "Knolli", true, new Timestamp(System.currentTimeMillis() - 1500000000));
        Enquiry e3 = new Enquiry("mandl@mandl.m", "Mandl", true, new Timestamp(System.currentTimeMillis() - 150000000000L));
	
        enquiries.add(e);
        enquiries.add(e2);
        enquiries.add(e3);
	*/
	
	if(first) {
	    setNewEnquiriesCount(enquiries.size());
	}
        
        Collections.sort(enquiries);
	first = false;
    }

    private JsonArray getEnquiries(AemsQueryAction qry) {
	try {
	    AemsResponse response = AemsAPI.call0(qry, null);
	    JsonArray result = response.getJsonArrayWithinObject();
	    return result;
	} catch(Exception ex) {
	    return new JsonArray();
	}
    }

    public int getNewEnquiriesCount() {
	return newEnquiriesCount;
    }

    public void setNewEnquiriesCount(int newEnquiriesCount) {
	this.newEnquiriesCount = newEnquiriesCount;
    }
    
    
    
    
    
    
}
