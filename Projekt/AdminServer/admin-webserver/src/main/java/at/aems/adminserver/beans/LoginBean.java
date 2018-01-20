/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans;

import at.aems.adminserver.UserRole;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Niggi
 */
@ManagedBean(name="login")
@RequestScoped
public class LoginBean implements Serializable { // Serializeable to allow application to run across multiple server nodes
    public static final int LOGGED_OUT_USER_ID = -1;
    private String username;
    private String password;
    
    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;
    
    public LoginBean() {
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
    
    public String doLogin() {
        int userId = 123; // TODO: Get userID from API!
        if(userId != -1) {
            userBean.setUserId(userId);
            userBean.setUsername(username);
            userBean.setPassword(password);
            if(username.equals("admin")) {
                userBean.setRole(UserRole.ADMIN);
            } else {
                userBean.setRole(UserRole.SUB_ADMIN);
            }
        }
        FacesContext context = FacesContext.getCurrentInstance();
        String viewId = context.getViewRoot().getViewId();
        return viewId;
    }
    
    public String doLogout() {
        userBean.setUserId(LOGGED_OUT_USER_ID);
        userBean.setUsername(null);
        userBean.setPassword(null);
        return "index";
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
    
    
    
}
