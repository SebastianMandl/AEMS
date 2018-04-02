/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Niggi
 */
public class SoftwareInfo {
    private String fileName;
    private String filePath;
    private String type;
    private Date releaseDate;
    
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    
    public static final String TYPE_ANDROID_APK = "Android APK";
    public static final String TYPE_RASPBERRY = "Raspberry PI Konfigurationssoftware";

    public SoftwareInfo() {
    }

    public SoftwareInfo(String fileName, String filePath, String version, String releaseDate) {
	this.fileName = fileName;
	this.filePath = filePath;
	this.type = version;
	try {
	   this.releaseDate = DATE_FORMAT.parse(releaseDate); 
	} catch(ParseException ex) {
	    throw new RuntimeException(ex);
	}
    }

    public String getFileName() {
	return fileName;
    }

    public void setFileName(String fileName) {
	this.fileName = fileName;
    }

    public String getFilePath() {
	return filePath;
    }

    public void setFilePath(String filePath) {
	this.filePath = filePath;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public Date getReleaseDate() {
	return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
	this.releaseDate = releaseDate;
    }
    
    public String getNiceReleaseDate() {
	return DATE_FORMAT.format(releaseDate);
    }
    
    
    
    
    
}
