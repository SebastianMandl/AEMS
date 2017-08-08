package at.htlgkr.aemsaccess;

import java.util.List;

import at.htlgkr.aemsaccess.fetcher.StatisticFetcher;
import at.htlgkr.aemsaccess.statistic.Statistic;

public class AemsDataApi {
  
  /** 
   * This string is used to authenticate at the aems api.
   * It contains the username and password, seperated by a colon:
   * "{@code <username>:<password>}"
   * */
  private String authString;
  
  /**
   * Initializes the AemsDataApi object.
   * @param authenticationString The string containing username and password in order to
   * authenticate at the aems api.
   * @see #authString
   */
  public AemsDataApi(String authenticationString) {
    this.authString = authenticationString;
  }
  
  public List<Statistic> getStatisticsOf(int ownerId) {
    StatisticFetcher fetcher = new StatisticFetcher(authString, ownerId);
    fetcher.fetchMetaOnly(false);
    return fetcher.bulkFetch();
  }

  /* More convenience methods go here */
  
}
