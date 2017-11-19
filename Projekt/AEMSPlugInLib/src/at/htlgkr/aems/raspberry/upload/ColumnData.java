package at.htlgkr.aems.raspberry.upload;

public class ColumnData {

	private String name;
	private String value;
	
	public ColumnData(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
}
