package at.htlgkr.aemsaccess.statistic;

import java.sql.Date;

public class StatisticTimes {
  private Integer timeId;
  private Integer optionsId;
  private Integer periodId;
  private Integer day;
  private Date startDate;
  private Date endDate;
  
  public StatisticTimes(Integer timeId, Integer optionsId, Integer periodId) {
    this.timeId = timeId;
    this.optionsId = optionsId;
    this.periodId = periodId;
  }
}
