/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.registration;

/**
 *
 * @author Niklas
 */
public class RegistrationData {
    private String username;
    private String password;
    private String email;
    private int plz;
    
    public RegistrationData() { }

    public RegistrationData(String username, String password, String email, int plz) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.plz = plz;
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

    public int getPlz() {
        return plz;
    }

    public void setPlz(int plz) {
        this.plz = plz;
    }
    
    
}
