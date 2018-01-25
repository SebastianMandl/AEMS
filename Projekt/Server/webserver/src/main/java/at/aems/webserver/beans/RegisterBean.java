/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.webserver.AemsUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Niklas
 */
@ViewScoped
@ManagedBean(name="register")
public class RegisterBean {
    
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
        
        if(isRegistering) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("home");
            } catch(IOException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            }
        }
        
        JsonObject obj = new JsonObject();
        JsonObject data = new JsonObject();
        
        data.addProperty("username", this.username);
        data.addProperty("password", this.password);
        data.addProperty("email", this.email);
        data.addProperty("plz", this.plz);
        
        obj.addProperty("action", "REGISTER");
        obj.add("data", data);
        
        String jsonString = new Gson().toJson(obj);
        
        AemsUtils.doPost(jsonString);
        System.out.println(jsonString);
        
        isRegistering = true;
        
        return "home";
    }
    
    
}
