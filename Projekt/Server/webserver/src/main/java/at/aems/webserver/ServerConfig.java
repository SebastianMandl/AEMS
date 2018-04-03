/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver;

import at.aems.apilib.AemsUser;
import at.aems.webserver.beans.objects.SoftwareInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

/**
 * This class is used to retrieve server wide configuration options.
 * The config file must be located at configuration/server_config.json
 * @author Niggi
 */
public class ServerConfig {
     
    public static final String DEFAULT_FILE_NAME = "configuration/server_config.json";

    private String apiUrl = "http://api.aems.at/graphql";
    private AemsUser masterCredentials = new AemsUser(0, "username", "password");
    private Integer connectTestTimeout = 5000;
    
    private List<SoftwareInfo> software = new ArrayList<>();
    
    public ServerConfig() {  
    } 
    
    public static ServerConfig read() {
	return read(DEFAULT_FILE_NAME);
    }
    
    public static ServerConfig read(String filename) {
	ServletContext servlet = (ServletContext) FacesContext.getCurrentInstance()
		.getExternalContext().getContext();
	String path = servlet.getRealPath("/");
	
	File configFile = new File(path, filename);
	if(!configFile.exists()) {
	    createConfig(configFile); 
	}
	 
	try {
	    String json = String.join("", Files.readAllLines(configFile.toPath()));
	    Gson g = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .serializeNulls()
                    .create();
	    
	    return g.fromJson(json, ServerConfig.class);
	} catch (IOException ex) {
	    Logger.getLogger(ServerConfig.class.getName()).log(Level.SEVERE, null, ex);
	}
	
	return null;
    }
    
    private static void createConfig(File file) {
	
	Gson g = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .serializeNulls()
                    .create();
	
	ServerConfig sc = new ServerConfig();
	sc.getSoftware().add(new SoftwareInfo("path/to/file.exe", "Nice Software", "01.04.2018"));
	String json = g.toJson(sc);
	
	try {
	    if(!file.exists()) {
		file.getParentFile().mkdirs();
		file.createNewFile(); 
	    }
	    Files.write(file.toPath(), json.getBytes());
	    System.out.println(" *** server config created ***");
	} catch(IOException e) {
	    throw new RuntimeException("cannot create server config");
	}
    }

    public String getApiUrl() {
	return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
	this.apiUrl = apiUrl;
    }
    
    public AemsUser getMaster() {
	return getMasterCredentials();
    }

    public AemsUser getMasterCredentials() {
	return masterCredentials;
    }

    public void setMasterCredentials(AemsUser masterCredentials) {
	this.masterCredentials = masterCredentials;
    }

    public Integer getConnectTestTimeout() {
	return connectTestTimeout;
    }

    public void setConnectTestTimeout(Integer connectTestTimeout) {
	this.connectTestTimeout = connectTestTimeout;
    }

    public List<SoftwareInfo> getSoftware() {
	return software;
    }

    public void setSoftware(List<SoftwareInfo> software) {
	this.software = software;
    }
    
    
    
    
    
    
}
