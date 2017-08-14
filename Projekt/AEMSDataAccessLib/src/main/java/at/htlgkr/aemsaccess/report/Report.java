package at.htlgkr.aemsaccess.report;

import java.util.List;

import at.htlgkr.aemsaccess.data.ApiData;
import at.htlgkr.aemsaccess.statistic.Statistic;

public class Report implements ApiData {
  
  private Integer reportId;
  private Integer userId;
  private String reportName;
  private String description;
  private Integer optionsId;
  private List<Statistic> statistics;
  
  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return super.toString();
  }
}
