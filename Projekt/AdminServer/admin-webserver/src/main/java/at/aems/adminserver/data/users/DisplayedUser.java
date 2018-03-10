/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.data.users;

import com.google.gson.JsonObject;

/**
 *
 * @author Niggi
 */
public class DisplayedUser {
    private Integer id;
    private String username;
    private String email;
    private String postalCode;

    public DisplayedUser() {
    }

    public DisplayedUser(Integer id, String username, String email, String post) {
        this.id = id;
        this.username = username;
        this.email = email;
	this.postalCode = post;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPostalCode() {
	return postalCode;
    }

    public void setPostalCode(String postalCode) {
	this.postalCode = postalCode;
    }
    
    
    public static DisplayedUser fromJsonObject(JsonObject o) {
	
	Integer id = o.has("id") ? o.get("id").getAsInt() : -1;
	String name = o.has("username") ? o.get("username").getAsString() : null;
	String email = o.has("email") ? o.get("email").getAsString() : null;
	String post = o.has("postal_code") ? o.get("postal_code").getAsString() : null;
	
	return new DisplayedUser(id, name, email, post);
	
    }
    
    
}
