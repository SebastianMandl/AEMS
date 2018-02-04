/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.apilib.AemsUser;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Niklas
 */
@ManagedBean(name = "user")
@SessionScoped
public class UserBean implements Serializable {

    private int userId;
    private String username;
    private String password;

    public UserBean() {
        userId = -1;
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

    public boolean isLoggedIn() {
        return userId != -1;
    }

    public AemsUser getAemsUser() {
        return new AemsUser(this.getUserId(), getUsername(), getPassword());
    }

    /**
     * Shortcut for userBean.getAemsUser().getAuthString()
     * @return The authentication string of the user that this bean holds.
     */
    public String getAuthenticationString() {
        return getAemsUser().getAuthString(null);
    }

    /**
     * @deprecated Gotta check if it is used somewhere.
     */
    public void checkLogin() {
        try {
            if (!isLoggedIn()) {
                FacesContext.getCurrentInstance().getExternalContext().redirect("index.xthml");
            }
        } catch (IOException e) {

        }
    }

}
