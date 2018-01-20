/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.data.users;

import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Niggi
 */
public class Enquiry {
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
    
    
    
}
