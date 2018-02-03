/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;
import javax.faces.bean.ManagedProperty;
import org.json.JSONObject;

/**
 *
 * @author Sebastian
 */
@ManagedBean
public class WebUIBean {
    
    @ManagedProperty(value="#{user}")
    private UserBean userBean;
    
    public ArrayList<String> getMeters() {
        // provisional
        /**
         * String data = req.getParameter("data"); // string
         * String action = req.getParameter("action").toUpperCase(); // string
         * String user = req.getParameter("user"); // int oder string
         * String authStr = req.getParameter("auth_str"); // string
         * String encryption = req.getParameter("encryption").toUpperCase(); // string
         * 
         * 
         * 
         * {meters=[{id=Sensor 1, user={username=x}}, {id=null, user={username=null}}]}
         * 
         * 
         * 
         */
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("{\n");
            builder.append("meters(is_sensor : \"true\") {\n") ;
            builder.append("id,\n");
            builder.append("user {\n");
            builder.append("username\n}");
            builder.append("}\n");
            builder.append("}");

            HttpURLConnection con = (HttpURLConnection) new URL("http://localhost:8084/AEMSWebService/RestInf?auth_str=" + userBean.getAuthenticationString() + "&user=" + userBean.getUserId() + "&data=" + Base64.getUrlEncoder().encodeToString(builder.toString().getBytes()) + "&action=QUERY" + "&ecryption=SSL").openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            if(con.getResponseCode() == 200) {
                // response ok
                builder = new StringBuilder();
                for(String line = reader.readLine(); line != null; reader.readLine()) {
                    builder.append(line);
                }
                JSONObject root = new JSONObject(new String(Base64.getUrlDecoder().decode(builder.toString())));
                
            } else {
                throw new NullPointerException("something went wrong while fetching data from REST-API!!!");
            }            
        } catch (IOException ex) {
            Logger.getLogger(WebUIBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
