package at.htlgkr.aemsaccess.statistic;

import java.util.List;

import at.htlgkr.aemsaccess.data.ApiData;

public class Statistic implements ApiData {
  
  private Integer statisticId;
  private Integer userId;
  private String statisticName;
  
  private Integer optionsId;
  private StatisticOptions options;
  
  private Integer reportId;
  private List<StatisticMeter> meters;
  
  public Statistic(Integer statisticId, Integer userId, String statisticName, Integer optionsId) {
    this.statisticId = statisticId;
    this.userId = userId;
    this.statisticName = statisticName;
    this.optionsId = optionsId;
  }

  public Integer getStatisticId() {
    return statisticId;
  }

  public void setStatisticId(Integer statisticId) {
    this.statisticId = statisticId;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getStatisticName() {
    return statisticName;
  }

  public void setStatisticName(String statisticName) {
    this.statisticName = statisticName;
  }

  public Integer getOptionsId() {
    return optionsId;
  }

  public void setOptionsId(Integer optionsId) {
    this.optionsId = optionsId;
  }

  public Integer getReportId() {
    return reportId;
  }

  public void setReportId(Integer reportId) {
    this.reportId = reportId;
  }
  
  
  
  
  
}
