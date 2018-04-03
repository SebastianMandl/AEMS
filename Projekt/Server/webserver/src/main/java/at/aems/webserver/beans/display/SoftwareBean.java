/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.display;

import at.aems.webserver.AemsUtils;
import at.aems.webserver.beans.objects.SoftwareInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public class SoftwareBean extends AbstractDisplayBean {

    private List<SoftwareInfo> availableSoftware;

    public SoftwareBean() {
	super();
    }

    @PostConstruct
    @Override
    public void init() {
	// do update even if no connection to api
	update();
    } 

    @Override
    public void update() {
	availableSoftware = AemsUtils.CONFIG.getSoftware();
	  
	/**
	 * sort by release date descending (newer versions first)
	 */
	Collections.sort(availableSoftware, new Comparator<SoftwareInfo>() {
	    @Override
	    public int compare(SoftwareInfo o1, SoftwareInfo o2) {
		return o2.getReleaseDate().compareTo(o1.getReleaseDate());
	    }
	});
    }

    public List<SoftwareInfo> getAvailableSoftware() {
	return availableSoftware;
    }

}
