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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.json.JSONArray;
import org.json.JSONObject;

import at.aems.apilib.AemsInsertAction;
import at.aems.apilib.crypto.EncryptionType;
import at.htlgkr.aems.database.AemsMeter.MeterType;
import at.htlgkr.aems.main.Main;
import at.htlgkr.aems.util.Logger.LogType;
import at.htlgkr.aems.util.crypto.Encrypter;
import at.htlgkr.aems.util.key.DiffieHellmanProcedure;

/**
 * This class is specifically designed to access and write data regarding
 * the back-end data collection. This includes data such as users (and
 * their credentials), meters, meter values and weather data. <p>
 * 
 * It is <b>not</b> made for accessing data from structures that were
 * created by the AEMS team, such as Statistics / Report Data.
 * @author Niklas
 */
public class AemsAPI {
  /* Before any static method is called, initialize the userList */
  static {
    initialize();
  }
  
  private static final String API_URL = "https://api.aems.at";
  private static List<AemsUser> userList;
  private static Map<AemsLocation, Double> temperatureMap;
  
  private static AemsInsertAction insertMeters;
  /**
   * This methods initializes this class by populating the {@link #userList}.
   */
  private static void initialize() {
    userList = new ArrayList<AemsUser>();
    temperatureMap = new HashMap<AemsLocation, Double>();
    insertMeters = new AemsInsertAction(null, EncryptionType.SSL);
    
    try {
      URL url = new URL("https://google.at"); // Just a placeholder for now
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      
      if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
        // MUST. DECRYPT. JSON
        // connection.getContent() [InputStream]
        
        String rawData = readDataFromStream(new FileInputStream("test-login.json"));
        JSONArray users = new JSONArray(rawData); 
        AemsUser aemsUser;
        for(int i = 0; i < users.length(); i++) {
          JSONObject user = users.getJSONObject(i);
          
          int id = user.getInt("id");
          String name = user.getString("username");
          String password = user.getString("password");
          
          aemsUser = new AemsUser(id, name, password);
          
          JSONArray meters = user.getJSONArray("meters");
          for(int j = 0; j < meters.length(); j++) {
            JSONObject meter = meters.getJSONObject(j);
            
            String meterId = meter.getString("id");
            MeterType type = MeterType.valueOf(meter.getString("type").toUpperCase());
            AemsLocation location = AemsLocation.fromJsonObject(meter.getJSONObject("location"));
            
            aemsUser.getMeters().add(new AemsMeter(meterId, type, location));
          }
          userList.add(aemsUser);
        }
        
      }
    } catch(Exception e) {
      Main.logger.log(LogType.WARN, "Error when initializing users list in AemsAPI! See log for details");
      Main.logger.log(LogType.WARN, e);
      e.printStackTrace();
    }
  }
  
  /**
   * Returns a newly instantiated List of all AemsUsers and their meters
   * @return A list of AemsUsers
   */
  public static List<AemsUser> getAemsUsers() {
    return new ArrayList<AemsUser>(userList);
  }
  
  public static void insertWeatherData(AemsLocation location, Double value) {
    temperatureMap.put(location, value);
  }
  
  public static void commitWeatherData() {
    
  }
  
  public static void insertMeterData(MeterValue value) {
    //IMPORTANT: Due to the way this is programmed it is possible that
    // rows could be inserted multiple times. On insert, the database will have
    // to check if an entry like this (same meterId and timestamp) already
    // exists. If so, do not insert this row.
    insertMeters.write("meter", value.getId());
    insertMeters.write("timestamp", value.getDate());
    insertMeters.write("measured_value", value.getValue());
    insertMeters.write("unit", "kWh");
    insertMeters.endWrite();
  }
  
  public static void commitMeterData() {
	  /*
	  try {
		  String json = insertMeters.toJson();
		  DiffieHellmanProcedure.sendKeyInfos(new Socket(InetAddress.getByName(API_URL), 9950));
		  byte[] clientKey = DiffieHellmanProcedure.confirmKey();
		  byte[] encryptedData = Encrypter.requestEncryption(clientKey, json.getBytes(StandardCharsets.UTF_8));
		  at.aems.apilib.AemsAPI.call(API_URL, "PUT", new String(encryptedData, "UTF-8"));
	  } catch(Exception e) {
		  Main.logger.log(LogType.ERROR, e);
	  } */
	 
  }
  

  private static String readDataFromStream(InputStream stream) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    StringBuffer buffer = new StringBuffer();
    String line;
    while((line = reader.readLine()) != null) {
      buffer.append(line);
    }
    return buffer.toString();
  }
}
