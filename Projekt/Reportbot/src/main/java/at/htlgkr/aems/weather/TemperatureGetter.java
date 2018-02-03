/**
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
 */
package at.htlgkr.aems.weather;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONObject;

import at.htlgkr.aems.database.AemsAPI;
import at.htlgkr.aems.database.AemsMeter;
import at.htlgkr.aems.database.AemsUser;
import at.htlgkr.aems.main.Main;
import at.htlgkr.aems.util.BotConfiguration;
import at.htlgkr.aems.util.Logger.LogType;

/**
 * This class makes use of the openweathermap API at
 * {@link api.openweathermap.org} to fetch temperature data. This temperature
 * data will considered when calculating electric consumptions
 * @author Niklas
 */
public class TemperatureGetter {
  
  private AemsUser user;
  private final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
  public static final double ABSOLUTE_ZERO = -273.15; // Will be error return value
  
  public TemperatureGetter(AemsUser user) {
    this.user = user;
  }
  
  public void updateTemperatures() {
    try {
      for(AemsMeter meter : user.getMeters()) {
        double temp = getTemperature(meter);
        Main.logger.log(LogType.DEBUG, "Temperature at " + meter.getLocation() + ": " + temp);
        AemsAPI.insertWeatherData(meter.getId(), meter.getLocation(), temp);
      }
      Main.logger.log(LogType.DEBUG, "Temperatures of meters of user " + user.getUsername() + " read!");
    } catch(Exception e) {
      Main.logger.log(LogType.ERROR, "Error when collecting weather data of user %0%", user.getUsername());
      Main.logger.log(LogType.ERROR, e);
    }
    
  }
  
  private double getTemperature(AemsMeter meter) throws Exception {
    String urlString = BASE_URL + meter.getLocation().getLocationAsQueryString();
    urlString += "&units=metric&APPID=";
    urlString += Main.config.get(BotConfiguration.API_KEY);
    
    URL url = new URL(urlString);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    
    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
      String rawData = readDataFromStream((InputStream) connection.getContent());
      JSONObject response = new JSONObject(rawData);
      JSONObject data = response.getJSONObject("main");
      return data.getDouble("temp");
    } else {
      return ABSOLUTE_ZERO;
    }
  }
  
  /**
   * by Sebastian Mandl
   */
  private String readDataFromStream(InputStream stream) throws IOException {
    StringBuffer buffer = new StringBuffer();
    while (stream.available() > 0) {
        buffer.append((char) stream.read());
    }
    return buffer.toString();
  }
}
