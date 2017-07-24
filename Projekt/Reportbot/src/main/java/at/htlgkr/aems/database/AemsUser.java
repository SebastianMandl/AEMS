package at.htlgkr.aems.database;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an user in the AEMS system.
 * @author Niklas
 *
 */
public class AemsUser {
  private int id;
  private String username;
  private String password;
  private List<AemsMeter> meters;
  
  public AemsUser(int id) {
    this(id, null, null);
  }
  
  @Deprecated
  /**
   * This constructor is deprecated, a user should always be created with an id.
   */
  public AemsUser(String username, String password) {
    this(0, username, password);
  }
  
  public AemsUser(int id, String username, String password) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.meters = new ArrayList<AemsMeter>();
  }
  
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public int getId() {
    return id;
  }
  public void setId(int id) {
    this.id = id;
  }
  public List<AemsMeter> getMeters() {
    return this.meters;
  }
  public void setMeters(List<AemsMeter> meters) {
    this.meters = meters;
  }
  
  @Override
  public String toString() {
    return "{AemsUser [" + id + ", " + username + ", " + password + "] }";
  }
}
