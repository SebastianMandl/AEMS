package at.htlgkr.aems.raspberry.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import at.htlgkr.aems.plugins.PlugIn;
import at.htlgkr.aems.raspberry.meter.InputDevice;
import at.htlgkr.aems.raspberry.upload.Authentication;
import at.htlgkr.aems.raspberry.upload.Uploader;
import at.htlgkr.aems.settings.MeterTypes;

public class PlugInManager {

	public static final ArrayList<PlugIn> PLUGINS_PREFABS = new ArrayList<>();
	public static final HashMap<String, InputDevice> METERS = new HashMap<>();
	public static final HashMap<String, InputDevice> SENSORS = new HashMap<>();
	
	public static final String DIRECTORY = "plugins";
	
	public static void unsetPluginForMeter(String meterId) {
		METERS.remove(meterId);
	}
	
	
	public static PlugIn getPluginByName(String name) {
		return PLUGINS_PREFABS.stream().filter(x -> {
			return x.getName().equals(name);
		}).toArray(size -> new PlugIn[size])[0];
	}
	
	public static PlugIn setPluginForMeter(String meterId, String port, PlugIn plugin) {
		PlugIn _plugin = plugin.clone();
		if(METERS.containsKey(meterId)) {
			METERS.remove(meterId);
			METERS.put(meterId, new InputDevice(meterId, port, _plugin));
		} else {
			METERS.put(meterId, new InputDevice(meterId, port, _plugin));
		}
		return _plugin;
	}
	
	public static PlugIn setPluginForSensor(String sensorName, String port, PlugIn plugin) {
		PlugIn _plugin = plugin.clone();
		if(SENSORS.containsKey(sensorName)) {
			SENSORS.remove(sensorName);
			SENSORS.put(sensorName, new InputDevice(sensorName, port, _plugin));
		} else {
			SENSORS.put(sensorName, new InputDevice(sensorName, port, _plugin));
		}
		return _plugin;
	}
	
	public static void runAllPlugins() {
		for(String key : METERS.keySet()) {
			METERS.get(key).getReader().start();
		}
		
		for(String key : SENSORS.keySet()) {
			SENSORS.get(key).getReader().start();
		}
	}
	
	public static void stopAllPlugins() {
		for(String key : METERS.keySet()) {
			METERS.get(key).getReader().stop();
		}
		
		for(String key : SENSORS.keySet()) {
			SENSORS.get(key).getReader().stop();
		}
	}
	
	public static PlugIn[] getPluginsForType(MeterTypes type) {
		return PLUGINS_PREFABS.stream().filter(x -> {
			if(x.getSetting().isSensor())
				return false;
			return x.getSetting().getMeterType().getType().equals(type);
		}).toArray(size -> new PlugIn[size]);
	}
	
	public static PlugIn[] getPluginsForSensors() {
		return PLUGINS_PREFABS.stream().filter(x -> {
			return x.getSetting().isSensor(); // do not check for unit equality since units can be specified as a string and hence can be arbitrary
		}).toArray(size -> new PlugIn[size]);
	}
	
	public static void loadPlugIns() {		
		File[] files = new File(DIRECTORY).listFiles();
		for(File file : files) {
			if(file.isFile() && file.getName().endsWith(".jar")) {
				try {
					URLClassLoader loader = new URLClassLoader(new URL[] {new URL("file:///" + file.getAbsolutePath())}, PlugInManager.class.getClassLoader());
						
					ZipInputStream is = new ZipInputStream(new FileInputStream(file));
					for(ZipEntry entry = is.getNextEntry(); entry != null; entry = is.getNextEntry()) {
						if(!entry.isDirectory() && entry.getName().endsWith(".class")) {
							
							@SuppressWarnings("unchecked")
							Class<PlugIn> clazz = (Class<PlugIn>) Class.forName(entry.getName().substring(0, entry.getName().length() - 6), true, loader);
							PLUGINS_PREFABS.add(clazz.newInstance());
						}
					}
					is.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}


	public static void setAuthentication(Authentication authentication) {
		for(String key : METERS.keySet()) {
			Uploader uploader = METERS.get(key).getPlugIn().getUploader();
			if(uploader != null) {
				uploader.setAuthentication(authentication);
			}
		}
		
		for(String key : SENSORS.keySet()) {
			Uploader uploader = SENSORS.get(key).getPlugIn().getUploader();
			if(uploader != null) {
				uploader.setAuthentication(authentication);
			}
		}
	}
	
}
