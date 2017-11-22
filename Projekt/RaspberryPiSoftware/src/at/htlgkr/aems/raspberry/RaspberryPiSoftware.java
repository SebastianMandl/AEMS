package at.htlgkr.aems.raspberry;

import java.util.Calendar;

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
		
		final int CYCLE_MINUTES = 10;
		final int CYCLE_INTERVAL = 1_000 * 60 * CYCLE_MINUTES;
		
		final Calendar CALENDAR = Calendar.getInstance();
		
		PlugInManager.loadPlugIns();
		DashboardConfigFrame.doInterface();
		
	}
	
}

