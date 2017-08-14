package at.htlgkr.aemsaccess.statistic;

import java.sql.Date;
import java.sql.Time;

import at.htlgkr.aemsaccess.data.ApiData;

public class StatisticException implements ApiData {
  private Integer exceptionId;
  private Integer optionsId;
  private StatisticOptions options;
  
  private Time operatingTimeStart;
  private Time operatingTimeEnd;
  private Boolean considerTemperature;
  private Integer expectedEnergyConsumption;
  
  private Integer periodId;
  private Integer day;
  private Date startDate;
  private Date endDate;
  
  public StatisticException(Integer exceptionId, Integer optionsId, Integer periodId) {
    super();
    this.exceptionId = exceptionId;
    this.optionsId = optionsId;
    this.periodId = periodId;
  }

  public Integer getExceptionId() {
    return exceptionId;
  }

  public void setExceptionId(Integer exceptionId) {
    this.exceptionId = exceptionId;
  }

  public Integer getOptionsId() {
    return optionsId;
  }

  public void setOptionsId(Integer optionsId) {
    this.optionsId = optionsId;
  }

  public StatisticOptions getOptions() {
    return options;
  }

  public void setOptions(StatisticOptions options) {
    this.options = options;
  }

  public Time getOperatingTimeStart() {
    return operatingTimeStart;
  }

  public void setOperatingTimeStart(Time operatingTimeStart) {
    this.operatingTimeStart = operatingTimeStart;
  }

  public Time getOperatingTimeEnd() {
    return operatingTimeEnd;
  }

  public void setOperatingTimeEnd(Time operatingTimeEnd) {
    this.operatingTimeEnd = operatingTimeEnd;
  }

  public Boolean getConsiderTemperature() {
    return considerTemperature;
  }

  public void setConsiderTemperature(Boolean considerTemperature) {
    this.considerTemperature = considerTemperature;
  }

  public Integer getExpectedEnergyConsumption() {
    return expectedEnergyConsumption;
  }

  public void setExpectedEnergyConsumption(Integer expectedEnergyConsumption) {
    this.expectedEnergyConsumption = expectedEnergyConsumption;
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
