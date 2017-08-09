package at.htlgkr.aemsaccess.data;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class represents a fetcher object that is capable of fetching
 * sets of data (JSONArrays). Thus, extending classes may only implement
 * the {@link #bulkFetch(Object...)} method. Also, the {@link #fetchJson()}
 * methods only return JSONArrays instead of regular Objects.
 * @author Niklas
 *
 */
public abstract class MultiItemFetcher extends AbstractFetcher {

  public MultiItemFetcher(String authString) {
    super(authString);
  }
  /**
   * The MultiItemFetcher class is not designed to fetch single pieces of data. This method is
   * unused and will throw an {@link UnsupportedOperationException}
   * @throws UnsupportedOperationException
   */
  @Override
  public final ApiData fetch(Object... params) {
    throw new UnsupportedOperationException("A MultiItemFetcher does not support the single fetch operation!");
  }

  @Override
  public abstract List<? extends ApiData> bulkFetch(Object... params);

  @Override
  public abstract String getSubUrl();
  
  @Override
  protected JSONArray fetchJsonFromUrl(String customUrl) {
    Object result = super.fetchJsonFromUrl(customUrl);
    if(result instanceof JSONArray) {
      return (JSONArray) result;
    }
    throw new JSONException("API-Call result could not be parsed into JSONArray --> " + result.toString());
  }
  
  @Override
  protected JSONArray fetchJson() {
    return fetchJsonFromUrl(baseURL + getSubUrl());
  }

}
