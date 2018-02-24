package com.example.knoll.aems;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsInsertAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.EncryptionType;

/**
 * Created by knoll on 22.02.2018.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private static final String PREFERENCE_KEY = "AemsLoginPreferenceKey";
    SharedPreferences sharedPreferences;
    int errorCount = 0;

    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //saveTokenInDatabase(refreshedToken);
    }


    private void saveTokenInDatabase(String refreshedToken) {

        sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        String username = sharedPreferences.getString("USERNAME", null);
        String password = sharedPreferences.getString("PASSWORD", null);
        String key = sharedPreferences.getString("SHAREDSECRETKEYSTRING", null);
        String userIdString = sharedPreferences.getString("USERID", null);
        int userId = Integer.parseInt(userIdString);

        AemsUser user = new AemsUser(userId, username, password);

        AemsInsertAction insert = new AemsInsertAction(user, EncryptionType.AES);
        insert.setTable("Benutzer");

        insert.beginWrite();
        insert.write("notificationToken", refreshedToken);

        byte[] sharedSecretKey = key.getBytes();

        insert.toJson(sharedSecretKey);
        AemsResponse response = null;

        AemsAPI.setUrl("https://api.aems.at");
        try {
            response = AemsAPI.call0(insert, sharedSecretKey);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int httpCode = response.getResponseCode();
        if (httpCode != 200 && errorCount<3){
            errorCount ++;
            saveTokenInDatabase(refreshedToken);
        }
        else if(errorCount>2){
            Log.w(TAG, "Notification-Key konnte nicht an den Server übermittelt werden");
        }
    }


}
