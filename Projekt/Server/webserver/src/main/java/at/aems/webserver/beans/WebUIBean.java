/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsLoginAction;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.NewMap;
import at.aems.webserver.beans.objects.Meter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Sebastian
 */
// I bims da Graf und I pfusch in deins Code
// Du brauchst die javax.faces.bean.ManagedBean, sunst wird des nix ;)
@ManagedBean
public final class WebUIBean {

    @ManagedProperty(value = "#{user}")
    private UserBean userBean;

    private final ArrayList<Meter> METERS = new ArrayList<>();
    private final ArrayList<Meter> SENSORS = new ArrayList<>();

    public ArrayList<Meter> getMETERS() {
        return METERS;
    }
    
    public ArrayList<Meter> getSENSORS() {
        return SENSORS;
    }

    private static final String REST_ADDRESS = "http://localhost:8084/AEMSWebService/RestInf?";

    public WebUIBean() {
        try {
            //Ich würde das auf die init() auslagern. 
            login(String.valueOf(userBean.getUserId()), "123456789", userBean.getAuthenticationString(), userBean.getUsername());
        } catch (Exception ex) {
            Logger.getLogger(WebUIBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        populateMeters();
    }
    
    @PostConstruct
    public void init() {
        // Execute logic that requires other beans here.
        // Bad exceptions may occur if other beans are accessed in the constructor.
    }

    public void populateMeters() {
        // provisional
        /**
         * String data = req.getParameter("data"); // string String action =
         * req.getParameter("action").toUpperCase(); // string String user =
         * req.getParameter("user"); // int oder string String authStr =
         * req.getParameter("auth_str"); // string String encryption =
         * req.getParameter("encryption").toUpperCase(); // string
         *
         *
         *
         * {meters=[{id=Sensor 1, user={username=x}}, {id=null,
         * user={username=null}}]}
         *
         *
         *
         */
        
        fetchUnit(false, METERS);
        fetchUnit(false, SENSORS);
    }

    private void fetchUnit(boolean isSensor, final ArrayList<Meter> REF) {
        try {
            /**
             * I see code covered by library. I shall
             * P R O P O S E
             * but it's up to you really, Seboostian
             */
            // <editor-fold defaultstate="collapsed" desc="The usage of the beautiful aems-apilib">
            /* UNCOMMENT PLS
            String query = AemsUtils.getQuery("sensors", NewMap.of("IS_SENSOR", isSensor));
            AemsQueryAction action = new AemsQueryAction(userBean.getAemsUser(), EncryptionType.SSL);
            action.setQuery(query);
            
            AemsAPI.setUrl(REST_ADDRESS);
            AemsResponse response = AemsAPI.call0(action, null);
            
            if(response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                JsonObject root = response.getAsJsonObject(true);   // true = response is encrypted
                JsonArray array = root.get("meters").getAsJsonArray();
                for(JsonElement item : array) {
                    JsonObject current = item.getAsJsonObject();
                    Meter meter = new Meter();
                    meter.setId(current.get("id").getAsString());
                    meter.setIsSensor(isSensor);

                    REF.add(meter);
                }
            }
                */
// </editor-fold>        
            StringBuilder builder = new StringBuilder();
            builder.append("{\n");
            builder.append("meters(is_sensor : \"").append(isSensor).append("\") {\n");
            builder.append("id\n");
            builder.append("}\n");
            builder.append("}");
            HttpURLConnection con = (HttpURLConnection) new URL(REST_ADDRESS + "auth_str=" + userBean.getAuthenticationString() + "&user=" + userBean.getUserId() + "&data=" + Base64.getUrlEncoder().encodeToString(builder.toString().getBytes()) + "&action=QUERY&ecryption=SSL").openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            if (con.getResponseCode() == 200) {
                // response ok
                builder = new StringBuilder();
                for (String line = reader.readLine(); line != null; reader.readLine()) {
                    builder.append(line);
                }
                JSONObject root = new JSONObject(new String(Base64.getUrlDecoder().decode(builder.toString())));
                JSONArray array = root.getJSONArray("meters");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    Meter meter = new Meter();
                    meter.setId(object.getString("id"));
                    meter.setIsSensor(isSensor);

                    REF.add(meter);
                }
            }
        } catch (Exception e) {
            // something went wrong while fetching from the REST-API
        }
    }

    private void login(String userId, String salt, String authStr, String username) throws Exception {  
        JSONObject object = new JSONObject();
        object.put("auth_str", authStr);
        object.put("salt", salt);
        object.put("user", username);

        byte[] data = object.toString().getBytes();

        HttpURLConnection con = (HttpURLConnection) new URL("http://localhost:8084/AEMSWebService/RestInf?user=" + userId + "&encryption=SSL&action=LOGIN&data=" + Base64.getUrlEncoder().encodeToString(data)).openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
            // login failed
        }
    }

}
