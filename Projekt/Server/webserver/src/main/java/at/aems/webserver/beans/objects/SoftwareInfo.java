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
    private String filePath;
    private String description;
    private Date releaseDate;
    
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    public SoftwareInfo() {
    }

    public SoftwareInfo(String filePath, String version, String releaseDate) {
	this.filePath = filePath;

	this.description = version;
	try {
	   this.releaseDate = DATE_FORMAT.parse(releaseDate); 
	} catch(ParseException ex) {
	    throw new RuntimeException(ex);
	}
    }

    public String getFileName() {
	return filePath.substring(filePath.lastIndexOf("/")+1);
    }

    public String getFilePath() {
	return filePath;
    }

    public void setFilePath(String filePath) {
	this.filePath = filePath;
    }

    public String getDesc() {
	return description;
    }

    public void setDesc(String type) {
	this.description = type;
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
