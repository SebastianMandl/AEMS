package at.htlgkr.aemsaccess.data;

import java.util.List;

/**
 * This interface is used to indicate that the implementing class is able to
 * gather information from the AEMS-Database by calling the AEMS-API.
 * The actual data will be fetched by calling the {@link #fetch(Object...)}
 * method, with the return type being a class that implements the {@link ApiData}
 * interface.
 * 
 * @author Niklas
 * @since 07.08.2017
 * @see ApiData
 */
public interface ApiFetcher {
  /**
   * This method is called to fetch data from the AEMS-API.
   * @param params Additional parameters
   * @return The fetch result
   */
  public ApiData fetch(Object...params);
  
  public List<? extends ApiData> bulkFetch(Object...params);
  
  /**
   * This method should return the sub url of this fetcher. For example, if
   * the fetcher is designed to fetch user data which can be found at
   * {@code https://myapi.com/users}, then the sub url could be {@code /users}
   * or {@code /users/<id>}
   * @return The sub url of this fetcher
   */
  public String getSubUrl();
}
