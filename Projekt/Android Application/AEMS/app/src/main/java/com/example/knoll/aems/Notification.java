package com.example.knoll.aems;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.EncryptionType;

/**
 * Created by knoll on 25.11.2017.
 */

public class Notification extends Activity {

    SharedPreferences sharedPreferences;
    private static final String TAG = "SpecificNotification";
    private static final String PREFERENCE_KEY = "AemsLoginPreferenceKey";
    String username = "";
    String password = "";
    String key = "";
    int userId = 0;

    int errorCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        username = sharedPreferences.getString("USERNAME", null);
        password = sharedPreferences.getString("PASSWORD", null);
        key = sharedPreferences.getString("SHAREDSECRETKEYSTRING", null);
        //String userIdString = sharedPreferences.getString("USERID", null);
        //userId = Integer.parseInt(userIdString);

        /*
        try {
            getDataFromServer();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
        loadAllNotifications();
    }

    private void getDataFromServer() throws JSONException {

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
            getDataFromServer();
        }
        else if(errorCount>2){
            Log.w(TAG, "Notification Infos konnten nicht vom Server geladen werden");
        }

        String decryptedResponse = response.getDecryptedResponse();

        //Get Data from JSON-Object
        String[] data = getDataFromJson(decryptedResponse);

        //Set Data
        TextView title = (TextView) findViewById(R.id.textViewZaehlerName);
        TextView type = (TextView) findViewById(R.id.textViewNotificationType);
        TextView reason = (TextView) findViewById(R.id.textViewReasonForNotification);
        TextView info = (TextView) findViewById(R.id.textViewFurtherInformation);

        title.setText(data[0]);
        type.setText(data[1]);
        reason.setText(data[2]);
        info.setText(data[3]);


    }

    private String[] getDataFromJson(String decryptedResponse) throws JSONException {

        JSONObject json = new JSONObject(decryptedResponse);
        String notificationName = json.getString("title");
        String notificationType = json.getString("type");
        String notificationReason = json.getString("reason");
        String furtherInformation = json.getString("infos");

        String[] data = new String[]{};
        data[0] = notificationName;

        data[1] = notificationType;
        data[2] = notificationReason;
        data[3] = furtherInformation;
        return data;
    }

    private void loadAllNotifications() {

        Button buttonLoadNotifications = (Button) findViewById(R.id.buttonListOfAllNotifications);

        buttonLoadNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notification.this, AllNotifications.class);
                startActivity(intent);
            }
        });

    }



}
