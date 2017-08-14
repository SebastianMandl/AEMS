package at.htlgkr.aemsaccess.statistic;

import at.htlgkr.aemsaccess.data.ApiData;

public class StatisticMeter implements ApiData {
  private Integer meterId;
  private Integer statisticId;
  
  public StatisticMeter(Integer meterId, Integer statisticId) {
    this.meterId = meterId;
    this.statisticId = statisticId;
  }

  public Integer getMeterId() {
    return meterId;
  }

  public void setMeterId(Integer meterId) {
    this.meterId = meterId;
  }

  public Integer getStatisticId() {
    return statisticId;
  }

  public void setStatisticId(Integer statisticId) {
    this.statisticId = statisticId;
  }
  
  
}
