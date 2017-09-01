package at.htlgkr.aemsaccess.data;

import java.io.IOException;
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
  
  public MultiItemFetcher(String authString, String baseUrl) {
    super(authString, baseUrl);
  }
  /**
   * The MultiItemFetcher class is not designed to fetch single pieces of data. This method is
   * unused and will throw an {@link UnsupportedOperationException}.
   * @throws UnsupportedOperationException If this method is called
   */
  @Override
  public final ApiData fetch(Object... params) {
    throw new UnsupportedOperationException("A MultiItemFetcher does not support the single fetch operation!");
  }

  @Override
  public abstract List<? extends ApiData> bulkFetch(Object... params);

  @Override
  public abstract String getSubUrl();
  
  /**
   * The MultiItemFetcher class is designed to aquire only a set of data. This
   * means that the result of the API-Call will be parsed into a JSONArray. If
   * this parse fails (e.g. because the API-Call returns a JSONObject), a
   * JSONException is thrown.
   * @return The result of the API-Call, as a JSONObject
   * @throws IOException If an error occurs while communicating with the AEMS API
   * @throws JSONException If the API-Call result cannot be parsed into a JSONObject
   */
  @Override
  protected JSONArray fetchJsonFromUrl(String customUrl) throws IOException {
    Object result = super.fetchJsonFromUrl(customUrl);
    if(result instanceof JSONArray) {
      return (JSONArray) result;
    }
    throw new JSONException("API-Call result could not be parsed into JSONArray --> " + result.toString());
  }
  
  @Override
  protected JSONArray fetchJson() throws IOException {
    return fetchJsonFromUrl(baseURL + getSubUrl());
  }

}
