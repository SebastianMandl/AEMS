/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import at.aems.apilib.AbstractAemsAction;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsDeleteAction;
import at.aems.apilib.AemsInsertAction;
import at.aems.apilib.AemsLoginAction;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUpdateAction;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.EncryptionType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Sebastian
 */
@ManagedBean(name = "webUIBean")
@SessionScoped
public final class WebUIBean {

    //@ManagedProperty(value = "#{user}")
    //private UserBean userBean;

    private final ArrayList<Meter> METERS = new ArrayList<>();
    private final ArrayList<Meter> SENSORS = new ArrayList<>();
    private final ArrayList<Anomaly> ANOMALIES = new ArrayList<>();
    
    private String selectedMeter;
    private String selectedSensor;
    private int cycleTime;
    
    AemsUser user = new AemsUser(185, "x", "pwd");
    
    private String script;
    private boolean editMode;
    private int toEditAnomalyId;
    
    public ArrayList<Anomaly> getANOMALIES() {
        return ANOMALIES;
    }
    
    public int getCycleTime() {
        return this.cycleTime;
    }
    
    public String getScript() {
        return script;
    }
    
    public void setScript(String script) {
        this.script = script;
    }
    
    public void setCycleTime(int cycleTime) {
        this.cycleTime = cycleTime;
    }

    public ArrayList<Meter> getMETERS() {
        return METERS;
    }
    
    public ArrayList<Meter> getSENSORS() {
        return SENSORS;
    }

    public String getSelectedMeter() {
	return selectedMeter;
    }

    public void setSelectedMeter(String selectedMeter) {
	this.selectedMeter = selectedMeter;
    }

    public String getSelectedSensor() {
	return selectedSensor;
    }

    public void setSelectedSensor(String selectedSensor) {
	this.selectedSensor = selectedSensor;
    }
    
    

    //private static final String REST_ADDRESS = AemsUtils.API_URL;
    private static final String REST_ADDRESS = "http://localhost:8084/AEMSWebService/RestInf";

    public WebUIBean() {
        AemsAPI.setUrl(REST_ADDRESS);
    }
    
    public String doEdit(Anomaly anomaly) {
        
        editMode = true;
        toEditAnomalyId = anomaly.getId();
        
        selectedMeter = anomaly.getMeter();
        selectedSensor = anomaly.getSensor();
        cycleTime = anomaly.getCycleTime();
        script = anomaly.getScript().replaceAll("<br />", "\n");
        
        populateMeters();
        return "index";
    }
    
    public String doDelete(Anomaly anomaly) {
        AemsDeleteAction action = new AemsDeleteAction(user, EncryptionType.SSL);
        action.enableSalt();
        action.setTable("anomalies");
        action.setIdColumn("id", anomaly.getId());
        try {
            AemsResponse response = AemsAPI.call0(action, null);
            if(response.getResponseCode() != 200) {
                throw new RuntimeException("something went wrong while uploading anomaly!!!");
            }
        } catch (IOException ex) {
            Logger.getLogger(WebUIBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        populateMeters();
        return "index";
    }
    
    public String doSubmit() {
        
        AbstractAemsAction a;
        
        if(editMode) {
            AemsUpdateAction action = new AemsUpdateAction(user, EncryptionType.SSL);
            action.setTable("anomalies");
            action.setIdColumn("id", toEditAnomalyId);
            action.write("script", script);
            action.write("meter", selectedMeter);
            action.write("sensor", selectedSensor);
            action.write("exec_intermediate_time", cycleTime);
            a = action;
            editMode = false;
        } else {
            AemsInsertAction action = new AemsInsertAction(user, EncryptionType.SSL);
            action.enableSalt();
            action.setTable("anomalies");
            action.write("script", script);
            action.write("meter", selectedMeter);
            action.write("sensor", selectedSensor);
            action.write("exec_intermediate_time", cycleTime);
            action.endWrite();
            a = action;
        }
        
        try {
            AemsResponse response = AemsAPI.call0(a, null);
            if(response.getResponseCode() != 200) {
                throw new RuntimeException("something went wrong while uploading anomaly!!!");
            }
        } catch (IOException ex) {
            Logger.getLogger(WebUIBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        populateMeters();
        return "index";
    }
    
    @PostConstruct
    public void init() {
//       try {
//            //Ich würde das auf die init() auslagern. 
//            //login(String.valueOf(userBean.getUserId()), "123456789", userBean.getAuthenticationString(), userBean.getUsername());
//             AemsLoginAction login = new AemsLoginAction(EncryptionType.SSL);
//             login.enableSalt();
//             login.setUsername("x");
//             login.setPassword("pwd");
//             AemsResponse response = AemsAPI.call0(login, null);
//             
//             if(response.getResponseCode() != 200)
//                 throw new Exception("login failed!");
//             
//             JsonParser parser = new JsonParser();
//             JsonObject object = parser.parse(response.getDecryptedResponse()).getAsJsonObject();
//             System.out.println(object);
//        } catch (Exception ex) {
//            Logger.getLogger(WebUIBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
        populateMeters();
    }

    public void populateMeters() {
        METERS.clear();
        SENSORS.clear();
        ANOMALIES.clear();
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
        fetchUnit(true, SENSORS);
        
        AemsQueryAction action = new AemsQueryAction(user, EncryptionType.SSL);
        action.enableSalt();

        action.setQuery("{ anomalies { id, meter { id }, sensor { name }, exec_intermediate_time, script_errors, script } }");
        
        try {
            AemsResponse response = AemsAPI.call0(action, null);
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(response.getDecryptedResponse()).getAsJsonObject();

            JsonArray array = object.getAsJsonArray("anomalies");
            for (int i = 0; i < array.size(); i++) {
                JsonObject oo = array.get(i).getAsJsonObject();
                if(!oo.has("id"))
                    continue; // signifies unauthorized access
                
                Anomaly a = new Anomaly();
                a.setMeter(oo.getAsJsonObject("meter").get("id").getAsString());
                a.setSensor(oo.getAsJsonObject("sensor").get("name").getAsString());
                a.setCycleTime(oo.get("exec_intermediate_time").getAsInt());
                String scriptErrors = oo.get("script_errors").getAsString();
                a.setScriptErrors(scriptErrors.toLowerCase().equals("null") ? "keine" : scriptErrors.replaceAll("\n", "<br />"));
                a.setScript(oo.get("script").getAsString().replaceAll("\n", "<br />"));
                a.setId(oo.get("id").getAsInt());
                
                ANOMALIES.add(a);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchUnit(boolean isSensor, final ArrayList<Meter> REF) {
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

        AemsQueryAction action = new AemsQueryAction(user, EncryptionType.SSL);
        action.enableSalt();

        action.setQuery("{ meters (is_sensor : \"" + isSensor + "\" ) { id } }");
        
        try {
            AemsResponse response = AemsAPI.call0(action, null);
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(response.getDecryptedResponse()).getAsJsonObject();

            JsonArray array = object.getAsJsonArray("meters");
            for (int i = 0; i < array.size(); i++) {
                JsonObject oo = array.get(i).getAsJsonObject();
                Meter meter = new Meter();
                meter.setId(oo.get("id").getAsString());
                meter.setIsSensor(isSensor);

                REF.add(meter);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
