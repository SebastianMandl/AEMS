/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webpage;

import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niklas
 */
@ManagedBean(name="user")
public class AemsUser {
    private String username;
    private String password;
    
    public AemsUser() {   
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
        return password != null && password.equals("hello") ? "statistiken" : "statistiken";
    }
}
