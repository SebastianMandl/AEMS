package at.htlgkr.aems.raspberry;

import at.htlgkr.aems.plugins.PlugIn;
import at.htlgkr.aems.raspberry.plugins.PlugInManager;
import at.htlgkr.aems.raspberry.reader.Reader;
import at.htlgkr.aems.raspberry.upload.AEMSUploader;
import at.htlgkr.aems.raspberry.upload.Authentication;
import at.htlgkr.aems.raspberry.upload.TableData;
import at.htlgkr.aems.raspberry.upload.UploadPackage;
import at.htlgkr.aems.raspberry.upload.Uploader;

public class Main {

	public static void main(String[] args) {
		PlugInManager.loadPlugIns();
		Uploader uploader = null;
		PlugIn p = null;
		for(PlugIn plugin : PlugInManager.PLUGINS) {
			plugin.getSetting().setPort("/dev/ttyUSB0");
			new Reader(plugin);
			uploader = new AEMSUploader(plugin);
			p = plugin;
		}
		uploader.upload(new UploadPackage().addData(new TableData("WeatherData").addData("meter", p.getSetting().getMeterId()).addData("temperature", "15.02").addData("id", "6")), new Authentication("admin", "admin"));
	}
	
}
