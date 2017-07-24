package at.htlgkr.aems.database;

/**
 * This class represents a geographical location which can be used in an
 * OpenWeatherMap query.
 * @author Niklas
 */
public class AemsLocation {
  
  private String locationName;
  private double latitude;
  private double longitude;
  
  public AemsLocation(String location) {
    this.locationName = location;
    this.latitude = 0;
    this.longitude = 0;
  }
  
  public AemsLocation(double latitude, double longitude) {
    this.locationName = null;
    this.latitude = latitude;
    this.longitude = longitude;
  }
  
  public boolean isSpecifiedByName() {
    return this.locationName != null;
  }
  
  public String getLocationAsQueryString() {
    if(isSpecifiedByName()) {
      return "?q=" + this.locationName;
    } else {
      return "?lat=" + this.latitude + "&lon=" + this.longitude;
    }
  }
  
  public String getLocationName() {
    return this.locationName;
  }
  
  public double getLatitude() {
    return this.latitude;
  }
  
  public double getLongitude() {
    return this.longitude;
  }
  
  @Override
  public String toString() {
    if(isSpecifiedByName()) {
      return this.locationName;
    }
    return "AemsLocation {lat="+this.latitude + ", long=" + this.longitude + "}";
  }
}
