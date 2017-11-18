package at.htlgkr.aems.raspberry;

import at.htlgkr.aems.plugins.PlugIn;
import at.htlgkr.aems.raspberry.plugins.PlugInManager;
import at.htlgkr.aems.raspberry.reader.Reader;

public class Main {

	public static void main(String[] args) {
		PlugInManager.loadPlugIns();
		for(PlugIn plugin : PlugInManager.PLUGINS) {
			plugin.getSetting().setPort("/dev/ttyUSB0");
			new Reader(plugin);
		}
	}
	
}
