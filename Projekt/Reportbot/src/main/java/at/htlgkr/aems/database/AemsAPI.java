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
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.GsonBuilder;

import at.aems.apilib.AemsInsertAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.crypto.EncryptionType;
import at.htlgkr.aems.main.Main;
import at.htlgkr.aems.util.BotConfiguration;
import at.htlgkr.aems.util.Logger.LogType;

/**
 * This class is specifically designed to access and write data regarding the
 * back-end data collection. This includes data such as users (and their
 * credentials), meters, meter values and weather data.
 * <p>
 * 
 * It is <b>not</b> made for accessing data from structures that were created by
 * the AEMS team, such as Statistics / Report Data.
 * 
 * @author Niklas
 */
public class AemsAPI {
    /* Before any static method is called, initialize the userList */
    static {
        API_URL = Main.config.get(BotConfiguration.API_URL);
        superUser = new at.aems.apilib.AemsUser(
                Main.config.getInt(BotConfiguration.SUPER_USER_ID, -1),
                Main.config.get(BotConfiguration.SUPER_USER_NAME), 
                Main.config.get(BotConfiguration.SUPER_USER_PWD)
        );
        initialize();
    }

    private static String API_URL;
    private static List<AemsUser> userList;

    private static at.aems.apilib.AemsUser superUser;
    private static AemsInsertAction insertMeters;
    private static AemsInsertAction insertWeather;

    /**
     * This methods initializes this class by populating the {@link #userList}.
     */
    private static void initialize() {
        userList = new ArrayList<AemsUser>();
        
        insertMeters = new AemsInsertAction(superUser, EncryptionType.SSL);
        insertMeters.setTable("MeterData");
        
        insertWeather = new AemsInsertAction(superUser, EncryptionType.SSL);
        insertWeather.setTable("WeatherData");
        
        at.aems.apilib.AemsAPI.setUrl(API_URL);
        try {
            AemsBotAction bot = new AemsBotAction(superUser, EncryptionType.SSL);
            AemsResponse resp = at.aems.apilib.AemsAPI.call0(bot, null);
            String rawData = resp.getDecryptedResponse();
            
            JSONObject responseObj = new JSONObject(rawData);
            String firstKey = responseObj.keys().next();
            
            JSONArray users = responseObj.getJSONArray(firstKey);
            AemsUser aemsUser;
            for (int i = 0; i < users.length(); i++) {
                JSONObject user = users.getJSONObject(i);

                int id = user.getInt("id");
                String name = user.getString("username");
                String password = user.getString("password");

                aemsUser = new AemsUser(id, name, password);

                JSONArray meters = user.getJSONArray("meters");
                for (int j = 0; j < meters.length(); j++) {
                    JSONObject meter = meters.getJSONObject(j);

                    String meterId = meter.getString("id");
                    Integer type = meter.getInt("type");
                    AemsLocation location = AemsLocation.fromJsonObject(meter.getJSONObject("location"));

                    aemsUser.getMeters().add(new AemsMeter(meterId, type, location));
                }
                userList.add(aemsUser);
            }

        } catch (Exception e) {
            Main.logger.log(LogType.WARN, "Error when initializing users list in AemsAPI! See log for details");
            Main.logger.log(LogType.WARN, e);
            e.printStackTrace();
        }
    }

    /**
     * Returns a newly instantiated List of all AemsUsers and their meters
     * 
     * @return A list of AemsUsers
     */
    public static List<AemsUser> getAemsUsers() {
        return new ArrayList<AemsUser>(userList);
    }

    public static void insertWeatherData(String id, AemsLocation location, Double value) {
        
        // round down to the previous hour
        GregorianCalendar c = new GregorianCalendar();
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        
        insertWeather.write("meter", id);
        // putting a "" before timestamp is important because otherwise
        // GSON would format it differently
        insertWeather.write("timestamp", "" + new Timestamp(c.getTimeInMillis()));
        insertWeather.write("temperature", value);
        insertWeather.endWrite();
    }

    public static void commitWeatherData() {
        try {
            at.aems.apilib.AemsAPI.setUrl(API_URL);
            Main.logger.log(LogType.INFO, new GsonBuilder().setPrettyPrinting().create().toJson(insertWeather.toJsonObject()));
            at.aems.apilib.AemsAPI.call(insertWeather, new byte[16]);
        } catch (IOException e) {
            Main.logger.log(LogType.ERROR, e);
        }
    }

    public static void insertMeterData(MeterValue value) {
        insertMeters.write("meter", value.getId());
        insertMeters.write("timestamp", "" + new Timestamp(value.getDate().getTime()));
        insertMeters.write("measured_value", value.getValue());
        insertMeters.write("unit", "kWh");
        insertMeters.endWrite();
    }

    public static void commitMeterData() {
        try {
            at.aems.apilib.AemsAPI.setUrl(API_URL);
            Main.logger.log(LogType.INFO, new GsonBuilder().setPrettyPrinting().create().toJson(insertMeters.toJsonObject()));
            at.aems.apilib.AemsAPI.call(insertMeters, new byte[16]);
            Main.logger.log(LogType.INFO, new GsonBuilder().setPrettyPrinting().create().toJson(insertMeters.toJsonObject()));
        } catch (IOException e) {
            Main.logger.log(LogType.ERROR, e);
        }
    }
}
