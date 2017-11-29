package at.htlgkr.aems.raspberry;

public class PortOption {

	private String title;
	private String value;
	
	public PortOption(String title, String value) {
		this.title = title;
		this.value = value;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return title;
	}
	
}
