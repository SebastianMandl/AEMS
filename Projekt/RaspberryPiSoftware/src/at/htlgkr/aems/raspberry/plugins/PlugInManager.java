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
import at.htlgkr.aems.raspberry.meter.Meter;
import at.htlgkr.aems.settings.MeterTypes;

public class PlugInManager {

	public static final ArrayList<PlugIn> PLUGINS = new ArrayList<>();
	public static final HashMap<String, Meter> METERS = new HashMap<>();
	
	public static final String DIRECTORY = "plugins";
	
	public static void setPluginForMeter(String meterId, String port, PlugIn plugin) {
		if(METERS.containsKey(meterId)) {
			METERS.get(meterId).reinitialize(meterId, port, plugin);
		} else {
			METERS.put(meterId, new Meter(meterId, port, plugin));
		}
	}
	
	public static void runAllPlugins() {
		for(String key : METERS.keySet()) {
			METERS.get(key).getReader().start();
		}
	}
	
	public static void stopAllPlugins() {
		for(String key : METERS.keySet()) {
			METERS.get(key).getReader().stop();
		}
	}
	
	public static PlugIn[] getPluginsForType(MeterTypes type) {
		return PLUGINS.stream().filter(x -> {
			return x.getSetting().getMeterType().getType().equals(type);
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
							PLUGINS.add(clazz.newInstance());
						}
					}
					is.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
