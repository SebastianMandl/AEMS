package at.htlgkr.aemsaccess.data;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class represents a fetcher object that is capable of fetching
 * single fields of data (JSONObjects). Thus, extending classes may only implement
 * the {@link #fetch(Object...)} method. Also, the {@link #fetchJson()}
 * methods only return JSONObjects instead of regular Objects.
 * @author Niklas
 *
 */
public abstract class SingleItemFetcher extends AbstractFetcher {

  public SingleItemFetcher(String authString) {
    super(authString);
  }
  
  public SingleItemFetcher(String authString, String baseUrl) {
    super(authString, baseUrl);
  }

  @Override
  public abstract ApiData fetch(Object... params);
  /**
   * The SingleItemFetcher class is designed to aquire only a field of data. This
   * means that the result of the API-Call will be parsed into a JSONObject. If
   * this parse fails (e.g. because the API-Call returns a JSONArray), a
   * JSONException is thrown.
   * @return The result of the API-Call, as a JSONObject
   * @throws IOException If an error occurs while communicating with the AEMS API
   * @throws JSONException If the API-Call result cannot be parsed into a JSONObject
   */
  @Override
  protected JSONObject fetchJsonFromUrl(String customUrl) throws IOException {
    Object result = super.fetchJsonFromUrl(customUrl);
    if(result instanceof JSONObject) {
      return (JSONObject) result;
    }
    throw new JSONException("API-Call result could not be parsed into JSONObject --> " + result.toString());
  }
  
  @Override
  protected JSONObject fetchJson() throws IOException {
    return fetchJsonFromUrl(baseURL + getSubUrl());
  }

  /**
   * The SingleItemFetcher class is not designed to fetch sets of data. This method is
   * unused and will throw an {@link UnsupportedOperationException}
   * @throws UnsupportedOperationException If this method is called
   */
  @Override
  public final List<? extends ApiData> bulkFetch(Object... params) {
    throw new UnsupportedOperationException("A SingleItemFetcher does not support the bulk fetch operation!");
  }

  @Override
  public abstract String getSubUrl();

}
