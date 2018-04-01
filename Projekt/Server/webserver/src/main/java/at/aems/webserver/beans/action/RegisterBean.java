/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.action;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsInsertAction;
import at.aems.apilib.AemsRegisterAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.beans.action.AbstractActionBean;
import at.aems.webserver.beans.objects.NotifyType;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author Niklas
 */
@ManagedBean(name="register")
public class RegisterBean extends AbstractActionBean {
    
    private String username;
    private String password;
    private String email;
    private String plz;
    private boolean netzonline;
    
    private String auth;
    
    private boolean isRegistering;
    
    private final AemsUser root = new AemsUser(215, "master", "pwd");
    
    public RegisterBean() {
        
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public boolean isNetzonline() {
        return netzonline;
    }

    public void setNetzonline(boolean netzonline) {
        this.netzonline = netzonline;
    }

    public boolean isRegistering() {
        return isRegistering;
    }
    
    
    public String doRegister() {
	/*
        if(!isRegistering) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("index");
            } catch(IOException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        */
        if(netzonline) {
            boolean passwordOk = checkPassword();
            if(!passwordOk) {
                notify.setMessage("Username und/oder Passwort stimmen nicht überein!");
                return "index";
            }
        }
        
        AemsRegisterAction action = new AemsRegisterAction(EncryptionType.SSL);
        action.setEmail(email);
        action.setUsername(username);
        action.setPassword(password);
	action.setNetzOnline(netzonline); 
        action.setPlz(plz); 
        AemsAPI.setUrl(AemsUtils.API_URL);
	AemsResponse registerResponse = null;
        try {
	    registerResponse = AemsAPI.call0(action, null);
	} catch(IOException ex) {
	    // yes 
	}
	if(registerResponse == null || !registerResponse.isOk()) {
	    notify.setMessage("Registration fehlgeschlagen", NotifyType.ERROR);
	    return "index";
	}
	
	AemsInsertAction insert = new AemsInsertAction(root);
	insert.setTable("registrations");
	insert.write("email", this.email);
	insert.write("timestamp", "" + new Timestamp(System.currentTimeMillis()));
	String code = UUID.randomUUID().toString().replaceAll("-", "");
	insert.write("confirm_code", code);
	insert.endWrite();
	
	String json = "{\"e\": \"" + email + "\", \"c\": \"" + code + "\"}";
	auth = json;
	
	try {
	    AemsAPI.call0(insert, null);
	} catch(IOException ex) {
	    
	}
	
        
        
        return "register.xhtml";
    }

    private boolean checkPassword() {
        
	configureApiParams();
        HttpClient client = HttpClients.createMinimal();
        HttpPost post = new HttpPost("https://netz-online.netzgmbh.at/eServiceWeb/j_security_check");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("j_username", username));
        params.add(new BasicNameValuePair("j_password", password));
        try {
            post.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(post);
	    int responseCode = response.getStatusLine().getStatusCode();
	    System.out.println(" *** register: " + username + "/" + password + ": " + responseCode);
            
            // 200 = Wrong Username/Password
            return response.getStatusLine().getStatusCode() != HttpStatus.SC_OK;
        } catch (Exception ex) {
            Logger.getLogger(RegisterBean.class.getName()).log(Level.SEVERE, null, ex);
	    return true;
        }
        
    }

    public String getAuth() {
	return auth;
    }
    
    
    
}
