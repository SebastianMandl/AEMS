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
package at.htlgkr.aems.database;

import org.json.JSONObject;

/**
 * This class represents a geographical location which can be used in an
 * OpenWeatherMap query.
 * @author Niklas
 */
public class AemsLocation {
  
  private String locationName;
  private double latitude;
  private double longitude;
  
  /**
   * Initializes the {@link AemsLocation} object with a location name
   * @param location The name of a a geographical location
   */
  public AemsLocation(String location) {
    this.locationName = location;
    this.latitude = 0;
    this.longitude = 0;
  }
  
  /**
   * Initializes the {@link AemsLocation} object with a latitude and longitude
   * value.
   * @param latitude The latitude value of a geographical location
   * @param longitude The longitude value of a geographical location
   */
  public AemsLocation(double latitude, double longitude) {
    this.locationName = null;
    this.latitude = latitude;
    this.longitude = longitude;
  }
  
  /**
   * This function provides information about whether this location is specified by
   * a location name <i>(for example: "Austria")</i> or geographical coordinates.
   * More precisely, this function returns {@code true} if the {@link #locationName}
   * is not null.
   * @return {@code true} if this location is specified by a name, {@code false} otherwise
   */
  public boolean isSpecifiedByName() {
    return this.locationName != null;
  }
  
  /**
   * This function returns the location in such a format that it could be
   * used in an URL for a OpenWeatherMap query {@code (api.openweahtermap.org)}
   * <p>
   * If the location is specified by name, the result would be
   * {@code "?q=<locationName>"}.
   * If the location is specified by geographical coordinates, the result would be
   * {@code "?lat=<latitude>&lon=<longitude>"}
   * @return The location, usable in an OpenWeatherMap query string
   * @see #isSpecifiedByName()
   */
  public String getLocationAsQueryString() {
    if(isSpecifiedByName()) {
      return "?q=" + this.locationName;
    } else {
      return "?lat=" + this.latitude + "&lon=" + this.longitude;
    }
  }
  
  public String getLocationName() {
    return this.locationName;
  }
  
  public double getLatitude() {
    return this.latitude;
  }
  
  public double getLongitude() {
    return this.longitude;
  }
  
  @Override
  public String toString() {
    if(isSpecifiedByName()) {
      return this.locationName;
    }
    return "AemsLocation {lat="+this.latitude + ", long=" + this.longitude + "}";
  }
  
  /**
   * Converts an AemsLocation (represented by a {@link JSONObject}) into an
   * AemsLocation object. The {@code location} must (at least) contain either a
   * {@code name} attribute or, if the {@code name} is {@code null}, a {@code lat} and a
   * {@code long} attribute (as a {@link Double} value).
   * @param location The location, as a JSONObject
   * @return The location, as an AemsLocation
   */
  public static AemsLocation fromJsonObject(JSONObject location) {
    if(location.get("name") != JSONObject.NULL) {
      return new AemsLocation(location.getString("name"));
    }
    double lat = location.getDouble("lat");
    double lng = location.getDouble("long");
    return new AemsLocation(lat, lng);
  }
}
