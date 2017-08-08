package at.htlgkr.aemsaccess.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class represents a basic fetcher object. A fetcher object should be
 * used to recieve data from the AEMS-API. Extending classes should implement
 * the {@link #fetch(Object...)} or the {@link #bulkFetch(Object...)} method(s)
 * (depending if the API-Call returns one piece of data or a set of data).
 * @author Niklas
 *
 */
public abstract class AbstractFetcher implements ApiFetcher {

  // Has to be replaced with link of actual API url
  protected String baseURL = "https://jsonplaceholder.typicode.com";
  
  /** 
   * This string is used to authenticate at the aems api.
   * It contains the username and password, seperated by a colon:
   * "{@code <username>:<password>}"
   * */
  protected String authenticationString;
  
  public AbstractFetcher(String authString) {
    this.authenticationString = authString;
  }
  
  public abstract ApiData fetch(Object...params);
  
  public abstract List<? extends ApiData> bulkFetch(Object...params);
  
  public abstract String getSubUrl();
  
  protected String readDataFromStream(InputStream stream) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    StringBuffer buffer = new StringBuffer();
    String line;
    while((line = reader.readLine()) != null) {
      buffer.append(line);
    }
    return buffer.toString();
  }
  
  /**
   * Uses the {@link #baseURL} and the result from the {@link #getSubUrl()} method to
   * create an url. This url will be used to recieve a json result.
   * @see #fetchJsonFromUrl(String)
   * @return
   */
  protected Object fetchJson() {
    return fetchJsonFromUrl(baseURL + getSubUrl());
  }
  
  /**
   * Executes a HTTP POST request against the {@code customUrl}. The result of this call
   * is supposed to be a JSON string. The JSON string is parsed into an appropriate 
   * structure (JSONObject / JSONArray) and returned.
   * @param customUrl 
   * @return The result of the call, represented as a JSON Structure
   */
  protected Object fetchJsonFromUrl(String customUrl) {
    try {
      URL url = new URL(customUrl);
      
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET"); //TODO: Set to POST
      connection.setDoOutput(true);
      connection.setRequestProperty("Authorization", "Basic " + encodeBase64(authenticationString));
      
      InputStream response = (InputStream) connection.getContent();
      String rawData = readDataFromStream(response);
      if(rawData.startsWith("[")) {
        return new JSONArray(rawData);
      } else {
        return new JSONObject(rawData);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    //
    return null;
  }

  /**
   * Encodes a given String into a Base 64 String.
   * @param string The string to be encoded
   * @return The encoded string
   */
  protected String encodeBase64(String string) {
    byte[] stringBytes = new byte[0];
    try {
      stringBytes = string.getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return Base64.getEncoder().encodeToString(stringBytes);
  }

}
