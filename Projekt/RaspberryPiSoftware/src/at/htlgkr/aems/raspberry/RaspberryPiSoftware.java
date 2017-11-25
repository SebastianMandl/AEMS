package at.htlgkr.aems.raspberry;

import at.htlgkr.aems.raspberry.plugins.PlugInManager;

/**
 * This class represents the software which is running on the 
 * raspberry pi. It is responsible for gathering information which
 * is sent over a serial connection by a meter (electricity, gas or water).
 * @author Sebastian
 * @since 04-08-2017
 */
public class RaspberryPiSoftware {
	

	public static void main(String[] args) {		
		PlugInManager.loadPlugIns();
		DashboardConfigFrame.doInterface();
	}
	
}

