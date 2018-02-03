package at.htlgkr.aems.raspberry.reader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.TimeUnit;

import at.htlgkr.aems.logger.Logger;
import at.htlgkr.aems.logger.Logger.LogType;
import at.htlgkr.aems.plugins.PlugIn;
import at.htlgkr.aems.raspberry.plugins.PlugInManager;
import at.htlgkr.aems.settings.ScriptFile;
import at.htlgkr.aems.settings.Setting;

public class Reader implements Runnable {
	
	private PlugIn plugin;
	
	private Setting setting;
	private ScriptFile file;
	
	private boolean shouldTerminate;
	private boolean isRunning;
	
	public Reader(PlugIn plugin) {
		this.plugin = plugin;
		
		this.setting = plugin.getSetting();
		this.file = plugin.getSetting().getScriptFile();
	}
	
	public void start() {
		shouldTerminate = false;
		
		while(isRunning); // while running await thread death
		isRunning = true;
		
		new Thread(this).start();
	}
	
	public void stop() {
		shouldTerminate = true;
	}
	
	@Override
	public void run() {
		while(!shouldTerminate) {
			// start process
			InputStream is = null;
			Process process = null;
			try {
				process = Runtime.getRuntime().exec(file.getExecutingProgramName() + " " + file.getScriptFile().getPath(), null, new File(PlugInManager.DIRECTORY));
				Logger.log(LogType.INFO, "Process for plugin \"%s\" was started!", plugin.getName());
				
				// log error to console in case the process fails to execute properly!
				if(process.waitFor(1, TimeUnit.SECONDS)) {
					Logger.log(LogType.ERROR, "Process for plugin \"%s\" has terminated", plugin.getName());
					try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
						for(String line = reader.readLine(); line != null; line = reader.readLine()) {
							Logger.log(LogType.ERROR, line);
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
					break;
				}
				
				// send init parameters to process hence to the "script"
				// json format: transmit port
				
				try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
					writer.write("{ port : \"" + setting.getPort() + "\" }\r\n");
					writer.flush();
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				is = process.getInputStream();
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// check for continuation and invoke plugin-function
			if(!plugin.readCyclic(plugin, is))
				break;
			
			try {
				if(process.isAlive())
					process.destroy();
				
				if(!process.isAlive())
					Logger.log(LogType.INFO, "Process for plugin \"%s\" was killed!", plugin.getName());
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(setting.getMillisUntilRepetition());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		isRunning = false;
	}
	
}
