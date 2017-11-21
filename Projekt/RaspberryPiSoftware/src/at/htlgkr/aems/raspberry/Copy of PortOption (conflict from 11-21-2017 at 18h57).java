package at.htlgkr.aems.raspberry;

import at.htlgkr.aems.plugins.PlugIn;
import at.htlgkr.aems.settings.MeterTypes;

public class PortOption {

	private String title;
	private String port;
	
	private MeterTypes type;
	private PlugIn plugin;
	
	public PortOption(String title, String port) {
		this.title = title;
		this.port = port;
	}
	
	public PortOption(PlugIn plugin, String title) {
		this.title = title;
		this.plugin = plugin;
	}
	
	public void setMeterType(MeterTypes type) {
		this.type = type;
	}
	
	public PlugIn getPlugIn() {
		return plugin;
	}
	
	public void setPlugin(PlugIn plugin) {
		this.plugin = plugin;
	}
	
	public MeterTypes getMeterType() {
		return type;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getPort() {
		return port;
	}
	
	@Override
	public String toString() {
		return title;
	}
	
}
