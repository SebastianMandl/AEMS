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
public class Responsibility {
    private String postalCode;
    private String name;

    public Responsibility(String postalCode, String name) {
        this.postalCode = postalCode;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return postalCode + " " + name;
    }
    
    public static Responsibility fromJsonObject(JsonObject o) {
	String post = o.has("postal_code") ? o.get("postal_code").getAsString() : null;
	String name = o.has("designation") ? o.get("designation").getAsString() : null;
	
	if(post != null && name != null) {
	    return new Responsibility(post, name);
	}
	return null;
    }
    
}
