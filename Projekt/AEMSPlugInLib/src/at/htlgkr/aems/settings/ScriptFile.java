package at.htlgkr.aems.settings;

import java.io.File;

public class ScriptFile {

	private String executingProgramName; // bash, perl, ...
	private File scriptFile;
	
	public ScriptFile(String programName, String pathToScriptFile) {
		this.executingProgramName = programName;
		scriptFile = new File(pathToScriptFile);
	}
	
	public String getExecutingProgramName() {
		return executingProgramName;
	}
	
	public File getScriptFile() {
		return scriptFile;
	}
	
}
