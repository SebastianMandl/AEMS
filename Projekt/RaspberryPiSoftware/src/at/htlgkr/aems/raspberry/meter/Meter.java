package at.htlgkr.aems.raspberry.meter;

import at.htlgkr.aems.plugins.PlugIn;
import at.htlgkr.aems.raspberry.reader.Reader;

public class Meter {

	private Reader reader;
	private PlugIn plugin;
	
	public Meter(String meterId, String port, PlugIn plugin) {
		reinitialize(meterId, port, plugin);
	}
	
	public void reinitialize(String meterId, String port, PlugIn plugin) {
		this.plugin = plugin;
		this.plugin.getSetting().setMeterId(meterId);
		this.plugin.getSetting().setPort(port);
		
		if(reader == null) {
			reader = new Reader(this.plugin);
		} else {
			reader.stop();
		}
	}
	
	public Reader getReader() {
		return reader;
	}
	
	public PlugIn getPlugIn() {
		return plugin;
	}
	
}
