package at.htlgkr.aems.exception;

import at.htlgkr.aems.database.AemsUser;

public class LoginFailedException extends Exception {
  public LoginFailedException() {
    super();
  }
  
  public LoginFailedException(AemsUser user) {
    super("Login failed, perhaps the password for user " + user.getUsername() + " is wrong!");
  }
}
