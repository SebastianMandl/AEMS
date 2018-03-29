package at.aems.webserver.beans.display;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import at.aems.apilib.AbstractAemsAction;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsDeleteAction;
import at.aems.apilib.AemsInsertAction;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUpdateAction;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.beans.objects.Meter;
import at.aems.webserver.beans.objects.Anomaly;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
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
public final class WebUIBean extends AbstractDisplayBean {

    //@ManagedProperty(value = "#{user}")
    //private UserBean userBean;
    private final ArrayList<Meter> METERS = new ArrayList<>();
    private final ArrayList<Meter> SENSORS = new ArrayList<>();
    private final ArrayList<Anomaly> ANOMALIES = new ArrayList<>();

    private String selectedMeter;
    private String selectedSensor;
    private int cycleTime;

    private AemsUser user;

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

    public WebUIBean() {
	/*
        AemsAPI.setCertPath("H:\\hubiC\\AEMS\\WebUIFinal\\keystore.cert");
        AemsAPI.setCertPassword("Minecraft=0");
        AemsAPI.setUrl(REST_ADDRESS);
	 */
    }

    @Override
    public void update() {
	user = userBean.getAemsUser();
	configureApiParams();
	populateMeters();
    }

    public String doEdit(Anomaly anomaly) {

	editMode = true;
	toEditAnomalyId = anomaly.getId();

	selectedMeter = anomaly.getMeter();
	selectedSensor = anomaly.getSensor();
	cycleTime = anomaly.getCycleTime();
	script = anomaly.getScript().replaceAll("<br />", "\n");

	populateMeters();
	updateWarningBean();
	return "einstellungenScripts";
    }

    public String doDelete(Anomaly anomaly) {
	AemsDeleteAction action = new AemsDeleteAction(user);
	action.setTable("anomalies");
	action.setIdColumn("id", anomaly.getId());
	try {
	    AemsResponse response = AemsAPI.call0(action, null);
	    if (response.getResponseCode() != 200) {
		throw new RuntimeException("something went wrong while uploading anomaly!!!");
	    }
	    updateWarningBean();
	} catch (IOException ex) {
	    Logger.getLogger(WebUIBean.class.getName()).log(Level.SEVERE, null, ex);
	}
	
	populateMeters();
	return "einstellungenScripts";
    }

    public String doSubmit() {

	AbstractAemsAction a;

	if (editMode) {
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
	    if (response.getResponseCode() != 200) {
		throw new RuntimeException("something went wrong while uploading anomaly!!!");
	    }
	    updateWarningBean();
	} catch (IOException ex) {
	    Logger.getLogger(WebUIBean.class.getName()).log(Level.SEVERE, null, ex);
	}

	populateMeters();
	return "einstellungenScripts";
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
		if (oo.get("id") == JsonNull.INSTANCE) {
		    continue; // signifies unauthorized access
		}
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
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void fetchUnit(boolean isSensor, final ArrayList<Meter> REF) {
	
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
		if (oo.get("id") == JsonNull.INSTANCE) {
		    continue;
		}
		meter.setId(oo.get("id").getAsString());
		meter.setIsSensor(isSensor);

		REF.add(meter);
	    }
	} catch (Exception e) {
	    e.printStackTrace(); 
	}
    }

    private void updateWarningBean() {
	AbstractDisplayBean warningBean = getDisplayBean("userWarningsBean");
	if (warningBean != null) {
	    warningBean.update();
	}
    }

}
