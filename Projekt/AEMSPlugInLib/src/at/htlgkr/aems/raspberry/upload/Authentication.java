package at.htlgkr.aems.raspberry.upload;

public class Authentication {

	private String id;
	private String username;
	private String password;
	
	public enum Encryption {
		SSL, AES;
	}
	
	private Encryption encryption;
	
	public Authentication(String username, String password, Encryption encryption) {
		this.username = username;
		this.password = password;
		this.encryption = encryption;
	}
	
	public String getEncryption() {
		return encryption.name();
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	@Override
	public String toString() {
		return username + ";" + password;
	}
	
}
