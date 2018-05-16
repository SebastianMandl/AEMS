/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.display;
 
import at.aems.webserver.AemsUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public class GeneratedReportsBean extends AbstractDisplayBean {

    private final File PDF_FOLDER = AemsUtils.CONFIG.getPdfFolderFile();
    private List<String> reports;
    
    @Override
    public void update() {
	reports = new ArrayList<>();
	File userFolder = new File(PDF_FOLDER, String.valueOf(userBean.getUserId()));
	if(!userFolder.exists()) {
	    return;
	}
	
	for(File pdfFile : userFolder.listFiles()) {
	    reports.add(pdfFile.getName());
	}

    }
      
    public String getDownloadString(String fileName) {
	return AemsUtils.CONFIG.getPdfFolder() + "/" + userBean.getUserId() + "/" + fileName;
    }

    public List<String> getReports() {
	return reports;
    }
    
    
}
