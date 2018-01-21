import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import at.htlgkr.aems.plugins.PlugIn;
import at.htlgkr.aems.raspberry.upload.AEMSUploader;
import at.htlgkr.aems.raspberry.upload.TableData;
import at.htlgkr.aems.raspberry.upload.UploadPackage;
import at.htlgkr.aems.settings.MeterTypes;
import at.htlgkr.aems.settings.ScriptFile;
import at.htlgkr.aems.settings.Setting;

public class AEMSElecricityPlugIn extends PlugIn {

	public AEMSElecricityPlugIn() {
		super("Electricity Meter", Setting.getSetting(MeterTypes.ELECTRICITY, new ScriptFile("ruby", "run.ruby")));
		super.getSetting().setMillisUntilRepetition(1000);
		super.setUploader(new AEMSUploader(this));
	}

	@Override
	public boolean readCyclic(PlugIn plugin, InputStream inputStream) {
		getUploader().setPlugIn(plugin);
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			for(String line = reader.readLine(); line != null; line = reader.readLine()) {
				System.out.println(line);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		super.getUploader().upload(new UploadPackage().addData(new TableData("weather_data").addData("meter", plugin.getSetting().getMeterId()).addData("temperature", "15.02").addData("id", "19")));
		
		return true;
	}
}
