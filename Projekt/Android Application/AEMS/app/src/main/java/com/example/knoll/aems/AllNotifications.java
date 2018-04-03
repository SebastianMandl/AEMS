package com.example.knoll.aems;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
    NotificationAdapter adapter;

    SharedPreferences sharedPreferences;
    private static final String TAG = "AllNotifications";
    private static final String PREFERENCE_KEY = "AemsLoginPreferenceKey";
    String username = "";
    String password = "";
    String key = "";
    int userId = 0;

    int errorCount = 0;

    ArrayList<String> notificationIds;
    ArrayList<String> titles;
    ArrayList<String> notificationTypes;
    ArrayList<Integer> images;
    ArrayList<String> meterId;
    ArrayList<String> sensors;
    ArrayList<String> furtherInformation;
    ArrayList<String> seen;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_notifications);

        sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        username = sharedPreferences.getString("USERNAME", null);
        password = sharedPreferences.getString("PASSWORD", null);
        key = sharedPreferences.getString("SHAREDSECRETKEYSTRING", null);
        String userIdString = sharedPreferences.getString("USERID", null);
        userId = Integer.parseInt(userIdString);

        loadData();

        backHome();

    }

    private void backHome() {
        Button backHome = (Button) findViewById(R.id.buttonBackToMainActivity);

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllNotifications.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    private void loadData() {

        notifications = (ListView) findViewById(R.id.listAllNotifications);

        final ProgressDialog progressDialog = new ProgressDialog(AllNotifications.this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Lade Benachrichtigungen...");
        progressDialog.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadNotifications();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                    }
                }, 4000);

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        progressDialog.dismiss();
        adapter = new NotificationAdapter(AllNotifications.this, titles, notificationTypes, images);
        notifications.setAdapter(adapter);
        updateNotifications();
    }


    private void loadNotifications() throws JSONException {

        AemsUser user = new AemsUser(userId, username, password);

        AemsQueryAction action = new AemsQueryAction(user, EncryptionType.SSL);
        action.setQuery("{notices {id, title, meter{id}, notificationtype{display_name}, sensor{name}, notice, seen}}"); // Hier muss meine GraphQL Query rein sensor, seen
        System.out.println("{notices {id, title, meter{id}, notice}}");

        AemsAPI.setTimeout(8000);
        AemsAPI.setUrl("http://aemsserver.ddns.net:8084/AEMSWebService/RestInf");
        AemsResponse response = null;
        try {
            response = AemsAPI.call0(action, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final int httpCode = response.getResponseCode();
        System.out.println("------------------------------" + httpCode + "------------------------------------------------------");

        if (httpCode != 200 && errorCount < 3) {
            errorCount++;
            loadNotifications();
        } else if (errorCount > 2) {
            Log.w(TAG, "Notifications konnten nicht vom Server geladen werden");
        }

        String decryptedResponse = response.getDecryptedResponse();
        System.out.println("Decrypted Response: -------------------- "+decryptedResponse);
        System.out.println("Response Exception---------------------- "+response.getExcetption());


        //Get Data from JSON-Object
        JSONObject[] jsons = getDataFromJson(decryptedResponse);

        titles = new ArrayList<>();
        notificationTypes = new ArrayList<>();
        images = new ArrayList<>();
        notificationIds = new ArrayList<>();
        meterId = new ArrayList<>();
        sensors = new ArrayList<>();
        furtherInformation = new ArrayList<>();
        seen = new ArrayList<>();


        for (int i = 0; i < jsons.length; i++) {
            seen.add(jsons[i].getString("seen"));
            if (seen.get(i).equals("false")) {
                notificationIds.add(jsons[i].getString("id"));
                titles.add(jsons[i].getString("title"));
                furtherInformation.add(jsons[i].getString("notice"));
                sensors.add(jsons[i].getJSONObject("sensor").getString("name"));
                notificationTypes.add(jsons[i].getJSONObject("notificationtype").getString("display_name"));
                images.add(R.drawable.logo_icon);
                meterId.add(jsons[i].getJSONObject("meter").getString("id"));
            }


            System.out.println(notificationIds);
            System.out.println(titles);
            System.out.println(meterId);
            System.out.println(furtherInformation);
            System.out.println(sensors);
            System.out.println(notificationTypes);
        }

    }

    private JSONObject[] getDataFromJson(String decryptedResponse) throws JSONException {

        JSONObject json = new JSONObject(decryptedResponse);
        JSONArray jsonArray = json.getJSONArray("notices");

        int size = jsonArray.length();
        ArrayList<JSONObject> arrays = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            JSONObject newJsonObject = jsonArray.getJSONObject(i);
            arrays.add(newJsonObject);
        }
        JSONObject[] myJsonObjectArray = new JSONObject[size];

        return arrays.toArray(myJsonObjectArray);
    }


    private void updateNotifications() {
        notifications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                ArrayList<String> notificationItem = new ArrayList<>();
                notificationItem.add(notificationIds.get(position));
                notificationItem.add(titles.get(position));
                notificationItem.add(meterId.get(position));
                notificationItem.add(furtherInformation.get(position));
                notificationItem.add(sensors.get(position));
                notificationItem.add(notificationTypes.get(position));

                System.out.println(notificationItem);
                //System.out.println(notificationItem.get(0) + " , " + notificationItem.get(1) + " , " + notificationItem.get(2));

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateDatabase(position);
                    }
                });
                thread.start();

                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(AllNotifications.this, Notification.class);
                intent.putStringArrayListExtra("notificationItem", notificationItem);
                startActivity(intent);
            }
        });
    }

    private void updateDatabase(final int position) {

        AemsUser user = new AemsUser(userId, username, password);

        AemsUpdateAction update = new AemsUpdateAction(user, EncryptionType.SSL);
        update.setTable("notices");
        update.setIdColumn("id", notificationIds.get(position));
        update.write("seen", true);

        AemsAPI.setUrl("http://aemsserver.ddns.net:8084/AEMSWebService/RestInf");
        AemsResponse response = null;
        try {
            response = AemsAPI.call0(update, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int httpCode = response.getResponseCode();
        if (httpCode != 200 && errorCount < 3) {
            errorCount++;
            updateDatabase(position);
        } else if (errorCount > 2) {
            Log.w(TAG, "Notification konnte nicht als gelesen markiert werden");
        }


    }
}
