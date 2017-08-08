package at.htlgkr.aemsaccess.statistic;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import at.htlgkr.aemsaccess.data.ApiData;

public class StatisticOptions implements ApiData {
  private Integer optionId;
  private Integer statisticId;
  
  /* Option Fields */
  private Time operatingTimeStart;
  private Time operatingTimeEnd;
  private Boolean considerTemperature;
  private Double expectedEnergyConsumption;
  // More options go here
  private List<StatisticTimes> times;
  
  public StatisticOptions(Integer optionId, Integer statisticId) {
    super();
    this.optionId = optionId;
    this.statisticId = statisticId;
    this.times = new ArrayList<StatisticTimes>();
  }
  
  public Integer getOptionId() {
    return optionId;
  }
  public void setOptionId(Integer optionId) {
    this.optionId = optionId;
  }
  public Integer getStatisticId() {
    return statisticId;
  }
  public void setStatisticId(Integer statisticId) {
    this.statisticId = statisticId;
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
  public Double getExpectedEnergyConsumption() {
    return expectedEnergyConsumption;
  }
  public void setExpectedEnergyConsumption(Double expectedEnergyConsumption) {
    this.expectedEnergyConsumption = expectedEnergyConsumption;
  }
  public List<StatisticTimes> getTimes() {
    return times;
  }
  public void setTimes(List<StatisticTimes> times) {
    this.times = times;
  }
  
  
  
  
}
