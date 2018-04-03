package at.aems.reportlib;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Config {
    private Integer userId = 0;
    private String username = "username";
    private String password = "password";
    private String pdfFolder = "pdfs";
    private String apiUrl = "http://aemsserver.ddns.net:8084/AEMSWebService/RestInf";
    
    /**
     * This constructor is required by gson and should not be used.
     * Use {@link #read} to read the configuration from a file.
     */
    public Config() {
    }
    
    public static Config read(String filename) {
        
        File file = new File(filename);
        if(!file.exists()) {
            throw new IllegalArgumentException("File " + filename + " does not exist!");
        }
        
        try {
            String rawJson = String.join("", Files.readAllLines(file.toPath()));
            Gson g = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .serializeNulls()
                    .create();
            return g.fromJson(rawJson, Config.class);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        
        return null;
        
    }
    
    public static void createIfMissing(String filename) {
        Gson g = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .serializeNulls()
                .create();
        try {
            File config = new File(filename);
            if(!config.exists()) {
                config.createNewFile();
                Files.write(config.toPath(), g.toJson(new Config()).getBytes());
            }
        } catch(Exception ex) { 
            ex.printStackTrace();
        }
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPdfFolder() {
        return pdfFolder;
    }
    
    public String getApiUrl() {
        return apiUrl;
    }

}
