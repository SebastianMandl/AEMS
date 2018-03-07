import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.htlgkr.aems.plugins.PlugIn;
import at.htlgkr.aems.raspberry.upload.AEMSUploader;
import at.htlgkr.aems.raspberry.upload.TableData;
import at.htlgkr.aems.raspberry.upload.UploadPackage;
import at.htlgkr.aems.settings.ScriptFile;
import at.htlgkr.aems.settings.Setting;

public class AEMSElecricityPlugIn extends PlugIn {

	public AEMSElecricityPlugIn() {
		super("Sensor PLUGIN", Setting.getSetting("kwH", new ScriptFile("py", "run.py")));
		super.getSetting().setMillisUntilRepetition(1000 * 60 * 15);
		super.setUploader(new AEMSUploader(this));
	}
	
	final Pattern NUMBER = Pattern.compile("(?<number>\\d+)");
	final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	public boolean readCyclic(PlugIn plugin, InputStream inputStream) {
		getUploader().setPlugIn(plugin);
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			for(String line = reader.readLine(); line != null; line = reader.readLine()) {
				Matcher matcher = NUMBER.matcher(line);
				if(matcher.find()) {
					String value = matcher.group("number");
					super.getUploader().upload(new UploadPackage().addData(new TableData("meter_data")
							.addData("meter", plugin.getSetting().getMeterId()).addData("measured_value", value).addData("timestamp", FORMAT.format(new Date())).addData("unit", getSetting().getSensorUnit())));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return true;
	}	
}
	//super.getUploader().upload(new UploadPackage().addData(new TableData("meter_data").addData("meter", plugin.getSetting().getMeterId()).addData("measured_value", "15.02").addData("id", "19")));
