package com.example.knoll.aems;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsDeleteAction;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUpdateAction;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.EncryptionType;

/**
 * Created by knoll on 25.11.2017.
 */

public class AllNotifications extends Activity {

    ListView notifications;

    //Delete
    //------------------------------
    ArrayList<Integer> idImages;
    ArrayList<String> titleList;
    ArrayList<String> infoList;
    ArrayList<String> notificationType;
    NotificationAdapter adapter;
    //------------------------------

    SharedPreferences sharedPreferences;
    private static final String TAG = "AllNotifications";
    private static final String PREFERENCE_KEY = "AemsLoginPreferenceKey";
    String username = "";
    String password = "";
    String key = "";
    int userId = 0;

    int errorCount = 0;
    ArrayList<String> titles;
    ArrayList<String> notificationTypes;
    ArrayList<Integer> images;
    ArrayList<Integer> notificationIds;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_notifications);

        sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        username = sharedPreferences.getString("USERNAME", null);
        password = sharedPreferences.getString("PASSWORD", null);
        key = sharedPreferences.getString("SHAREDSECRETKEYSTRING", null);
        //String userIdString = sharedPreferences.getString("USERID", null);
        //userId = Integer.parseInt(userIdString);

        loadData();

    }

    private void loadData() {

        //loadNotifications();

        notifications = (ListView) findViewById(R.id.listAllNotifications);

        //Delete
        //-----------------------------------------------
        idImages = new ArrayList<>();
        titleList = new ArrayList<>();
        notificationType = new ArrayList<>();

        idImages = getImageList();
        titleList = getTitleList();
        notificationType = getNotificationTypleList();

        adapter = new NotificationAdapter(AllNotifications.this, titleList, notificationType, idImages);
        notifications.setAdapter(adapter);
        //------------------------------------------------

        updateNotifications();

    }

    private void loadNotifications() throws JSONException {

        AemsUser user = new AemsUser(userId, username, password);

        AemsQueryAction action = new AemsQueryAction(user, EncryptionType.AES);
        action.setQuery(""); // Hier muss meine GraphQL Query rein

        byte[] sharedSecretKey = key.getBytes();
        action.toJson(sharedSecretKey);

        AemsAPI.setUrl("https://api.aems.at");
        AemsResponse response = null;
        try {
            response = AemsAPI.call0(action, sharedSecretKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int httpCode = response.getResponseCode();
        if (httpCode != 200 && errorCount<3){
            errorCount ++;
            loadNotifications();
        }
        else if(errorCount>2){
            Log.w(TAG, "Notifications konnten nicht vom Server geladen werden");
        }

        String decryptedResponse = response.getDecryptedResponse();

        //Get Data from JSON-Object
        JSONObject[] jsons = getDataFromJson(decryptedResponse);

        titles = new ArrayList<>();
        notificationTypes = new ArrayList<>();
        images = new ArrayList<>();
        notificationIds = new ArrayList<>();


        for(int i = 0; i< jsons.length; i++){
            titles.add(jsons[i].getString("title"));
            notificationTypes.add(jsons[i].getString("type"));
            images.add(R.drawable.logo_icon);
            String id = (jsons[i].getString("id"));
            notificationIds.add(Integer.parseInt(id));
        }

        adapter = new NotificationAdapter(AllNotifications.this, titles, notificationTypes, images);
        notifications.setAdapter(adapter);
    }


    private JSONObject[] getDataFromJson(String decryptedResponse) throws JSONException {

        JSONObject json = new JSONObject(decryptedResponse);
        JSONArray jsonArray = json.getJSONArray("notifications");

        int size = jsonArray.length();
        ArrayList<JSONObject> arrays = new ArrayList<>();
        for(int i = 0; i< size; i++){
            JSONObject newJsonObject = jsonArray.getJSONObject(i);
            arrays.add(newJsonObject);
        }
        JSONObject[] jsons = new JSONObject[arrays.size()];

        return jsons;
    }


    private void updateNotifications() {
        notifications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //updateDatabase(position);

                Intent intent = new Intent(AllNotifications.this, Notification.class);
                startActivity(intent);
            }
        });
    }

    private void updateDatabase(int position) {

        AemsUser user = new AemsUser(userId, username, password);

        AemsDeleteAction delete = new AemsDeleteAction(user, EncryptionType.AES);
        delete.setTable("Notifications");

        delete.setIdColumn("id", notificationIds.get(position));

        byte[] sharedSecretKey = key.getBytes();
        delete.toJson(sharedSecretKey);

        AemsAPI.setUrl("https://api.aems.at");
        AemsResponse response = null;
        try {
            response = AemsAPI.call0(delete, sharedSecretKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int httpCode = response.getResponseCode();
        if (httpCode != 200 && errorCount<3){
            errorCount ++;
            updateDatabase(position);
        }
        else if(errorCount>2){
            Log.w(TAG, "Notification konnte nicht vom Server gelöscht werden");
        }

    }



    //Delete
    //------------------------------------------------------------
    public ArrayList<String> getTitleList(){
        titleList = new ArrayList<>();
        titleList.add("Notification 1");
        titleList.add("Notification 3");
        titleList.add("Notification 4");
        titleList.add("Notification 7");
        titleList.add("Notification 21");

        return titleList;
    }

    public ArrayList<Integer> getImageList(){
        idImages = new ArrayList<>();
        idImages.add(R.drawable.logo_icon);
        idImages.add(R.drawable.logo_icon);
        idImages.add(R.drawable.logo_icon);
        idImages.add(R.drawable.logo_icon);
        idImages.add(R.drawable.logo_icon);

        return idImages;
    }


    public ArrayList<String> getNotificationTypleList(){
        notificationType = new ArrayList<>();
        notificationType.add("Warnung");
        notificationType.add("Benachrichtigung");
        notificationType.add("Benachrichtigung");
        notificationType.add("Warnung");
        notificationType.add("Benachrichtigung");

        return notificationType;
    }
    //------------------------------------------------------------

}
