/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.webserver.AemsUtils;
import java.io.IOException;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * This managed bean class is designated to serve as the login interface by calling
 * the #doLogin() method.
 * @author Niklas
 */
@ManagedBean(name="login")
@RequestScoped
public class LoginBean implements Serializable { // Serializeable to allow application to run across multiple server nodes
    private String username;
    private String password;
    
    @ManagedProperty(value = "#{user}")
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
        int userId = AemsUtils.getUserId(username, password);
        if(userId != -1) {
            userBean.setUserId(userId);
            userBean.setUsername(username);
            userBean.setPassword(password);
        }
        FacesContext context = FacesContext.getCurrentInstance();
        String viewId = context.getViewRoot().getViewId();
        return viewId;
    }
    
    public String doLogout() {
        userBean.setUserId(-1);
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
