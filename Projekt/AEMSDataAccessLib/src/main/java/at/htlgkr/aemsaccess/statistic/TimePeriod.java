package at.htlgkr.aemsaccess.statistic;

import at.htlgkr.aemsaccess.data.ApiData;

public class TimePeriod implements ApiData {
  private Integer periodId;
  private String displayName;
  
  public TimePeriod(Integer id, String name) {
    this.periodId = id;
    this.displayName = name;
  }

  public Integer getPeriodId() {
    return periodId;
  }

  public void setPeriodId(Integer periodId) {
    this.periodId = periodId;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
  
  
}
