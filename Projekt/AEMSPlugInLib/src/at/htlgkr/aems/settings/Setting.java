package at.htlgkr.aems.settings;

public class Setting implements Cloneable {
	
	private String meterId;
	private MeterType meterType;
	
	private boolean isSensor;
	private String sensorUnit;
	
	private ScriptFile file;
	
	private String port;
	private long millisUntilRepetition = 15 * 1000 * 60; // 15 minutes as we store the values in the database in a 15 minutes period;
	
	private Setting(MeterType meter, ScriptFile file) {
		this.meterType = meter;
		setScriptFile(file);
	}
	
	private Setting(String sensorUnit, ScriptFile file) {
		this.isSensor = true;
		this.sensorUnit = sensorUnit;
		setScriptFile(file);
	}
	
	public boolean isSensor() {
		return isSensor;
	}
	
	public void isSensor(boolean isSensor) {
		this.isSensor = isSensor;
	}
	
	public void setSensorUnit(String sensorUnit) {
		this.sensorUnit = sensorUnit;
	}
	
	public String getSensorUnit() {
		return sensorUnit;
	}
	
	public void setMeterId(String id) {
		this.meterId = id;
	}
	
	public String getMeterId() {
		return meterId;
	}
	
	public void setScriptFile(ScriptFile file) {
		this.file = file;
	}
	
	public ScriptFile getScriptFile() {
		return file;
	}
	
	public MeterType getMeterType() {
		return meterType;
	}
	
	public void setMillisUntilRepetition(long millis) {
		this.millisUntilRepetition = millis;
	}
	
	public long getMillisUntilRepetition() {
		return millisUntilRepetition;
	}
	
	/**
	 * will be assigned by the gui or automatically
	 * @param port
	 */
	public void setPort(String port) {
		this.port = port;
	}
	
	public String getPort() {
		return port;
	}
	
	public static Setting getSetting(MeterTypes type, ScriptFile file) {
		return new Setting(new MeterType(type), file);
	}
	
	public static Setting getSetting(String sensorUnit, ScriptFile file) {
		return new Setting(sensorUnit, file);
	}
	
	@Override
	public Setting clone() {
		Setting setting = new Setting(meterType, file);
		setting.setMeterId(meterId);
		setting.setMillisUntilRepetition(millisUntilRepetition);
		setting.setPort(port);
		return setting;
	}
	
}
