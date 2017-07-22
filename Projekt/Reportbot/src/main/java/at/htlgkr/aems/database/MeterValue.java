package at.htlgkr.aems.database;

import java.util.Date;

public class MeterValue {
  private String meterId;
  private Date date;
  private double value;
  private boolean valid;
  
  public MeterValue(Date date, double value, String validString) {
    this.date = date;
    this.value = value;
    this.valid = "VALID".equals(validString);
  }
  
  public Date getDate() {
    return date;
  }
  public void setDate(Date date) {
    this.date = date;
  }
  public double getValue() {
    return value;
  }
  public void setValue(double value) {
    this.value = value;
  }
  public boolean isValid() {
    return valid;
  }
  public void setValid(boolean valid) {
    this.valid = valid;
  }
  public String getId() {
    return this.meterId;
  }
  public void setId(String id) {
    this.meterId = id;
  }
  
}
