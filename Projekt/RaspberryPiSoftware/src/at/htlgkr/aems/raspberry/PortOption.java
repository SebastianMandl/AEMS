package at.htlgkr.aems.raspberry;

import at.htlgkr.aems.plugins.PlugIn;
import at.htlgkr.aems.settings.MeterTypes;

public class PortOption {

	private String title;
	private String port;
	
	private MeterTypes type;
	private PlugIn plugin;
	
	private boolean isSensor;
	private String sensorUnit;
	
	public PortOption(String title, String port) {
		this.title = title;
		this.port = port;
	}
	
	// title is either the meter id or the sensor unit
	public PortOption(PlugIn plugin, String title, String port, boolean isSensor) {
		this.title = title;
		this.plugin = plugin;
		this.port = port;
		this.isSensor = isSensor;
		this.sensorUnit = title;
	}
	
	public boolean isSensor() {
		return isSensor;
	}
	
	public String getSensorUnit() {
		return sensorUnit;
	}
	
	public void setMeterType(MeterTypes type) {
		this.type = type;
	}
	
	public void setSensorUnit(String unit) {
		this.sensorUnit = unit;
	}
	
	public void isSensor(boolean isSensor) {
		this.isSensor = isSensor;
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
