package at.htlgkr.aems.raspberry.upload;

import at.htlgkr.aems.plugins.PlugIn;

public abstract class Uploader {

	protected PlugIn plugin;
	protected Authentication authentication;
	
	public Uploader(PlugIn plugin) {
		this.plugin = plugin;
	}
	
	public PlugIn getPlugIn() {
		return plugin;
	}
	
	
	public void setPlugIn(PlugIn plugin) {
		this.plugin = plugin;
	}
	
	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}
	
	public void upload(UploadPackage _package) {
		upload(_package, authentication);
	}
	
	/**
	 * This method enables to upload so called upload-packages
	 * which are basically sets of data tied to multiple tables in the database.
	 * 
	 * Authentication my be required in case a server or database is accepting the data.
	 * 
	 */
	public abstract void upload(UploadPackage _package, Authentication authentication);
	
}
