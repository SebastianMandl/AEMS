import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import at.htlgkr.aems.plugins.PlugIn;
import at.htlgkr.aems.settings.ScriptFile;
import at.htlgkr.aems.settings.Setting;

public class AEMSElecricityPlugIn extends PlugIn {

	public AEMSElecricityPlugIn() {
		super("Sensor (kwH)", Setting.getSetting("kwH", new ScriptFile("ruby", "run.ruby")));
		super.getSetting().setMillisUntilRepetition(1000);
	}

	@Override
	public boolean readCyclic(InputStream inputStream) {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			for(String line = reader.readLine(); line != null; line = reader.readLine()) {
				System.out.println(line);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}

}
