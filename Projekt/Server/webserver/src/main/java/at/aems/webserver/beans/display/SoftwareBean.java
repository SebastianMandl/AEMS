/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.display;

import at.aems.webserver.beans.objects.SoftwareInfo;
import java.util.ArrayList;
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
	availableSoftware = new ArrayList<>();

	add("android_aems-1.0.0.apk", "apk/AEMS_1.0.0.apk", SoftwareInfo.TYPE_ANDROID_APK, "31.03.2018");
	add("raspberry-gui-aems-1.0.0.jar", "apk/AEMS_1.0.0.apk", SoftwareInfo.TYPE_RASPBERRY, "01.04.2018");

	add("dei-mama-exe", "apk/mama.exe", "Anderes", "31.03.1955");

    }

    public List<SoftwareInfo> getAvailableSoftware() {
	return availableSoftware;
    }

    private void add(String name, String path, String type, String date) {
	this.availableSoftware.add(new SoftwareInfo(name, path, type, date));
    }

}
