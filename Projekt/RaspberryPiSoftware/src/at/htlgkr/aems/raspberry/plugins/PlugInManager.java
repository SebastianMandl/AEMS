package at.htlgkr.aems.raspberry.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import at.htlgkr.aems.plugins.PlugIn;

public class PlugInManager {

	public static final ArrayList<PlugIn> PLUGINS = new ArrayList<>();
	
	public static final String DIRECTORY = "plugins";
	
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
