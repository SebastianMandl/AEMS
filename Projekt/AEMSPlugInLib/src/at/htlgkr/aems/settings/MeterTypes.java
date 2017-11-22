package at.htlgkr.aems.settings;

public enum MeterTypes {
	WATER("Liter"), ELECTRICITY("kwH"), GAS("m*m");
	
	private String unit;
	
	MeterTypes(String unit) {
		this.unit = unit;
	}
	
	public String getUnit() {
		return unit;
	}
}