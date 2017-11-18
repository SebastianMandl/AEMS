package at.htlgkr.aems.plugins;

import java.io.InputStream;

import at.htlgkr.aems.settings.Setting;

public abstract class PlugIn {

	// internal name which will be consulted by the PlugInManager
	private String name;
	private Setting setting;
	
	public PlugIn(String name, Setting setting) {
		this.name = name;
		this.setting = setting;
	}
	
	/**
	 * This method is invoked cyclically.
	 * The time until the cycle is repeated is set by the settings value millisUntilRepetiton!
	 * InputStream of the file which is currently read from
	 * @return
	 */
	public abstract boolean readCyclic(InputStream inputStream);
	
	public String getName() {
		return name;
	}
	
	public Setting getSetting() {
		return setting;
	}
	
}
