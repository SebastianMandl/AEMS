package at.htlgkr.aems.database;

public class AemsMeter {
  
  
  public enum MeterType {
    ELECTRIC, WATER, GAS;
  }
  
  private String id;
  private MeterType type;
  private AemsLocation location;
  
  public AemsMeter(String id, MeterType type, AemsLocation location) {
    this.id = id;
    this.type = type;
    this.location = location;
  }
  
  public AemsMeter(String id) {
    this(id, null, null);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public MeterType getType() {
    return type;
  }

  public void setType(MeterType type) {
    this.type = type;
  }

  public AemsLocation getLocation() {
    return location;
  }

  public void setLocation(AemsLocation location) {
    this.location = location;
  }
  
  

}
