package at.htlgkr.aemsaccess.fetcher;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import at.htlgkr.aemsaccess.data.AbstractFetcher;
import at.htlgkr.aemsaccess.data.ApiData;
import at.htlgkr.aemsaccess.statistic.Statistic;

public class StatisticFetcher extends AbstractFetcher {

  private Integer statisticOwnerId;
  
  /**
   * If this boolean is set to {@code true}, then only metadata of statistics will be fetched.
   * This includes:
   * <ul>
   *    <li>StatisticID, UserID, StatisticName</li>
   *    <li>Statistic Options</li>
   *    <li>Statistic Meters</li>
   * </ul>
   * By default, this boolean is set to {@code false}
   */
  private boolean fetchMetadataOnly;
  
  public StatisticFetcher(String authString, Integer ownerId) {
    super(authString);
    this.statisticOwnerId = ownerId;
  }

  @Override
  public ApiData fetch(Object... params) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Statistic> bulkFetch(Object... params) {
    JSONArray jsonStatistics = (JSONArray) fetchJson();
    List<Statistic> result = new ArrayList<Statistic>();
    
    // Parse JSON result into the result list
    
    return result;
  }

  @Override
  public String getSubUrl() {
    return "/statisticsof/" + statisticOwnerId;
  }
  
  /**
   * @see #fetchMetadataOnly
   * @param shouldOnlyFetchMetadata
   */
  public void fetchMetaOnly(boolean shouldOnlyFetchMetadata) {
    this.fetchMetadataOnly = shouldOnlyFetchMetadata;
  }

}
