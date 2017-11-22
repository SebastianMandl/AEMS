package at.htlgkr.aems.settings;

import java.util.ArrayList;

public class Setting implements Cloneable {
	
	private String meterId;
	private MeterType meterType;
	private ArrayList<Position> positions;
	
	private ScriptFile file;
	
	private String port;
	private long millisUntilRepetition;
	
	private Setting(MeterType meter, ArrayList<Position> positions, ScriptFile file) {
		this.meterType = meter;
		this.positions = positions;
		millisUntilRepetition = 15 * 1000 * 60; // 15 minutes as we store the values in the database in a 15 minutes period
		setScriptFile(file);
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
	
	public Position[] getPositions() {
		return positions.toArray(new Position[positions.size()]);
	}
	
	public static Setting getSetting(MeterTypes type, ScriptFile file, Position... positions) {
		ArrayList<Position> list = new ArrayList<>(positions.length);
		for(Position pos : positions) {
			list.add(pos);
		}
		return new Setting(new MeterType(type), list, file);
	}
	
	@Override
	public Setting clone() {
		ArrayList<Position> pos = new ArrayList<>(positions.size());
		for(Position position : positions) {
			pos.add(position);
		}
		Setting setting = new Setting(meterType, pos, file);
		setting.setMeterId(meterId);
		setting.setMillisUntilRepetition(millisUntilRepetition);
		setting.setPort(port);
		return setting;
	}
	
}
