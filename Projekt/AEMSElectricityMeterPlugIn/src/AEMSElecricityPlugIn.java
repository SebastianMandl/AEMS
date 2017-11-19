import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import at.htlgkr.aems.plugins.PlugIn;
import at.htlgkr.aems.settings.MeterTypes;
import at.htlgkr.aems.settings.Position;
import at.htlgkr.aems.settings.ScriptFile;
import at.htlgkr.aems.settings.Setting;

public class AEMSElecricityPlugIn extends PlugIn {

	public AEMSElecricityPlugIn() {
		super("ElectricityMeter", Setting.getSetting("AT00000000000000000003333", MeterTypes.ELECTRICITY, new ScriptFile("ruby", "run.ruby"), new Position("total consumption", "1\\.8\\.1")));
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
