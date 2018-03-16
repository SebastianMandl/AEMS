/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.action;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsRegisterAction;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.beans.action.AbstractActionBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.swing.AbstractAction;
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
    
    private boolean isRegistering;
    
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
                return "home";
            }
        }
        
        AemsRegisterAction action = new AemsRegisterAction(EncryptionType.SSL);
        action.setEmail(email);
        action.setUsername(username);
        action.setPassword(password);
	action.setNetzOnline(netzonline); 
        action.setPlz(plz); 
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(action.toJsonObject()));
        AemsAPI.setUrl(AemsUtils.API_URL);
        try {
	    AemsAPI.call0(action, null);
	} catch(IOException ex) {
	    // yes 
	}
        
        
        return "register.xhtml";
    }

    private boolean checkPassword() {
        
        HttpClient client = HttpClients.createMinimal();
        HttpPost post = new HttpPost("https://netz-online.netzgmbh.at/eServiceWeb/j_security_check");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("j_username", username));
        params.add(new BasicNameValuePair("j_password", password));
        try {
            post.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(post);
            
            // timeout means that it worked
            return response.getStatusLine().getStatusCode() == HttpStatus.SC_REQUEST_TIMEOUT;
        } catch (Exception ex) {
            Logger.getLogger(RegisterBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
}
