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

  public Integer getTimeId() {
    return timeId;
  }

  public void setTimeId(Integer timeId) {
    this.timeId = timeId;
  }

  public Integer getOptionsId() {
    return optionsId;
  }

  public void setOptionsId(Integer optionsId) {
    this.optionsId = optionsId;
  }

  public Integer getPeriodId() {
    return periodId;
  }

  public void setPeriodId(Integer periodId) {
    this.periodId = periodId;
  }

  public Integer getDay() {
    return day;
  }

  public void setDay(Integer day) {
    this.day = day;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }
  
  
}
