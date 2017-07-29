package at.htlgkr.aems.exception;

import at.htlgkr.aems.database.AemsUser;
import at.htlgkr.aems.util.Utils;

public class LoginFailedException extends Exception {

  private static final long serialVersionUID = 3045743990606377988L;

  public LoginFailedException() {
    super();
  }
  
  public LoginFailedException(AemsUser user) {
    super("Login for user " + Utils.getUserDetails(user) + " failed!");
  }
}
