/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans;

import at.aems.adminserver.UserRole;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsUser;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Niggi
 */

@ManagedBean
@SessionScoped
public class UserBean implements Serializable {

    private int userId = -1;
    private String username;
    private String password;
    private UserRole role;

    public UserBean() {
    }
    
    @PostConstruct
    public void init() {
        role = UserRole.ADMIN;
	AemsAPI.setUrl("http://10.10.0.167:8084/AEMSWebService/RestInf");
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isLoggedIn() {
        return userId != -1;
    }
    
    public boolean isAdmin() {
        return isLoggedIn() && role == UserRole.ADMIN;
    }
    
    public boolean isSubAdmin() {
        return isLoggedIn() && (role == UserRole.ADMIN || role == UserRole.SUB_ADMIN);
    }
    
    public AemsUser getAemsUser() {
        return new AemsUser(this.getUserId(), getUsername(), getPassword());
    }

    public String getAuthenticationString() {
        String userCredentials = userId + ":" + username + ":" + password;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(userCredentials.getBytes(StandardCharsets.UTF_8));

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void checkLogin() {
        try {
            if (!isLoggedIn()) {
                FacesContext.getCurrentInstance().getExternalContext().redirect("index.xthml");
            }
        } catch (IOException e) {
            
        }
    }

}
