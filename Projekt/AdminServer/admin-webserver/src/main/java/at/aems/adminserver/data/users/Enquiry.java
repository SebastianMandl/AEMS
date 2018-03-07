/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.data.users;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Niggi
 */
public class Enquiry implements Comparable<Enquiry>{
    private String email;
    private String username;
    private boolean useNetzonline;
    private Timestamp signupTime;

    public Enquiry() {
    }

    public Enquiry(String email, String username, boolean useNetzonline, Timestamp signupTime) {
        this.email = email;
        this.username = username;
        this.useNetzonline = useNetzonline;
        this.signupTime = signupTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isUseNetzonline() {
        return useNetzonline;
    }

    public void setUseNetzonline(boolean useNetzonline) {
        this.useNetzonline = useNetzonline;
    }

    public Timestamp getSignupTime() {
        return signupTime;
    }

    public void setSignupTime(Timestamp signupTime) {
        this.signupTime = signupTime;
    }
    
    public String getNiceSignupTime() {
	if(signupTime == null) {
	    return "Keine SignupTime gesetzt!";
	}
        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar then = new GregorianCalendar();
        then.setTimeInMillis(signupTime.getTime());
        
        long difference = now.getTimeInMillis() - then.getTimeInMillis();
        long dayDifference = TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
        
        String niceString = "";
        if(dayDifference < 1) {
            niceString = "Anfrage vor wenigen Stunden gesendet";
        } else if(dayDifference < 30) {
            niceString = "Anfrage vor " + dayDifference + " Tag"
                    + (dayDifference == 1 ? "" : "en") + " gesendet";
        } else {
            niceString = "Anfrage vor über einem Monat gesendet";
        }
        
        return niceString;
    }

    @Override
    public int compareTo(Enquiry o) {
	if(signupTime == null || o.getSignupTime() == null) {
	    return 0;
	}
        Timestamp t1 = this.getSignupTime();
        Timestamp t2 = o.getSignupTime();
        if(t1 == null || t2 == null) {
            return 0;
        }
        return t1.before(t2) ? 1 : -1;
    }
    
    public static Enquiry fromJsonObject(JsonObject o) {
	String email = o.has("email") ? o.get("email").getAsString() : null;
	String user = o.has("username") ? o.get("username").getAsString() : null;
	boolean netz = o.has("use_netzonline") ? o.get("use_netzonline").getAsBoolean() : false;
	
	String time = o.has("member_since") ? o.get("member_since").getAsString() : null;
	Timestamp stamp = null;
	
	if(time != null && !time.equals("null")) {
	    stamp = Timestamp.valueOf(time);
	}
	return new Enquiry(email, user, netz, stamp);
    }
    
    private static Object get(JsonObject o, String key) {
	
	if(o.has(key)) {
	    JsonPrimitive p = o.get(key).getAsJsonPrimitive();
	    if(p.isNumber())
		return p.getAsNumber();
	    if(p.isBoolean())
		return p.getAsBoolean();
	    if(p.isString()) {
		return p.getAsString();
	    }
	}
	
	return null;
	
    }
    
}
