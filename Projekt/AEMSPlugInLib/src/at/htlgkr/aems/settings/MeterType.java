package at.htlgkr.aems.settings;

public class MeterType {
	
	private MeterTypes type;
	
	public MeterType(MeterTypes type) {
		this.type = type;
	}
	
	public MeterTypes getType() {
		return type;
	}
	
	public String getUnit() {
		return type.getUnit();
	}
	
}
