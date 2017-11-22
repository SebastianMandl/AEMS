package at.htlgkr.aems.raspberry.upload;

public class Authentication {

	private String username;
	private String password;
	
	public Authentication(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
}
