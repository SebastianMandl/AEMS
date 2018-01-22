package at.htlgkr.aems.plugins;

import java.io.InputStream;

import at.htlgkr.aems.raspberry.upload.Uploader;
import at.htlgkr.aems.settings.Setting;

public abstract class PlugIn implements Cloneable {

	// internal name which will be consulted by the PlugInManager
	private String name;
	private Setting setting;
	
	private Uploader uploader;
	
	public PlugIn(String name, Setting setting) {
		this.name = name;
		this.setting = setting;
	}
	
	public void setUploader(Uploader uploader) {
		this.uploader = uploader;
	}
	
	public Uploader getUploader() {
		return uploader;
	}
	
	/**
	 * This method is invoked cyclically.
	 * The time until the cycle is repeated is set by the settings value millisUntilRepetiton!
	 * InputStream of the file which is currently read from
	 * @return
	 */
	public abstract boolean readCyclic(PlugIn plugin, InputStream inputStream);
	
	public String getName() {
		return name;
	}
	
	public Setting getSetting() {
		return setting;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public PlugIn clone() {
		final PlugIn _this = this;
		PlugIn plugin = new PlugIn(name, setting.clone()) {
			
			@Override
			public boolean readCyclic(PlugIn plugin, InputStream inputStream) {
				return _this.readCyclic(plugin, inputStream);
			}
		};
		plugin.setUploader(uploader);
		return plugin;
	}
	
}
