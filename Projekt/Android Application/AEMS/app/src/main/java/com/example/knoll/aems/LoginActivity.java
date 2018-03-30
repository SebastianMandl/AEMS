package com.example.knoll.aems;

/**
 * Created by knoll on 03.10.2017.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsInsertAction;
import at.aems.apilib.AemsLoginAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.EncryptionType;
import butterknife.ButterKnife;
import butterknife.Bind;


public class LoginActivity extends Activity {
    private static final String TAG = "LoginActivity";
    private static final String PREFERENCE_KEY = "AemsLoginPreferenceKey";
    SharedPreferences sharedPreferences;
    int httpCode = 0;
    String decryptedText;
    String userId = "";
    String username = "";
    String password = "";
    int errorCount = 0;

    @Bind(R.id.input_username)
    EditText _inputUsername;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.btn_login)
    Button _loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authentifizierung...");
        progressDialog.show();

        username = _inputUsername.getText().toString();
        password = _passwordText.getText().toString();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                loginToAEMS();
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        progressDialog.dismiss();

        // On complete call either onLoginSuccess or onLoginFailed
        if (httpCode == 200) {
            System.out.println("-------------------------------onLoginSuccess()--------------------------------------------");
            onLoginSuccess();
        } else {
            onLoginFailed();
        }
        progressDialog.dismiss();

    }


    private void loginToAEMS() {

        String key = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        try {
            //http://aemsserver.ddns.net:8084/AEMSWebService/AAA
            //http://10.0.6.41:8084/AEMSWebService/AAA?android=android
            //http://10.0.0.25:8084/AEMSWebService/AAA?android=android

            AemsAPI.setUrl("http://aemsserver.ddns.net:8084/AEMSWebService/RestInf");

            AemsLoginAction loginAction = new AemsLoginAction(EncryptionType.SSL);
            loginAction.setUsername(username);
            loginAction.setPassword(password);
            AemsResponse response = null;

            try {
                response = AemsAPI.call0(loginAction, null);
                decryptedText = response.getDecryptedResponse();
                JSONObject json = new JSONObject(decryptedText);
                userId = json.getString("id");

                httpCode = response.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("----------------------------------    " + httpCode + "   ----------------------------------------");

            signNotifications(username, password, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        System.out.println("-------------------------- We are in the onLoginSuccess() -------------------------------");
        //Save Logindata in SharedPreference
        sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        String user = sharedPreferences.getString("USERNAME", null);
        String passw = sharedPreferences.getString("PASSWORD", null);


        if (user == null && passw == null) {
            System.out.println("----------------------------- user null und password null --------------------------------------");
            CheckBox checkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxRememberLogin);

            sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
            sharedPreferences.edit().putString("USERNAME", username).commit();
            sharedPreferences.edit().putString("PASSWORD", password).commit();
            sharedPreferences.edit().putString("USERID", userId).commit();
            String uId = sharedPreferences.getString("USERID", null);
            System.out.println("User Id: ---------------------- " + uId);
            sharedPreferences.edit().putBoolean("REMEMEBERME", checkBoxRememberMe.isChecked()).commit();
        }
        finish();

    }

    private void signNotifications(String username, String password, String userIdString) {

        sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        String token = sharedPreferences.getString("NOTIFICATIONTOKEN", null);
        boolean notificationTokenSigned = sharedPreferences.getBoolean("NOTIFICATIONTOKENSIGNED", false);
        System.out.println(token);
        System.out.println(notificationTokenSigned);

        if (!notificationTokenSigned) {

            int userId = Integer.parseInt(userIdString);
            AemsUser user = new AemsUser(userId, username, password);

            AemsInsertAction insert = new AemsInsertAction(user, EncryptionType.SSL);
            insert.setTable("notification_tokens");

            insert.beginWrite();
            insert.write("token", token);
            insert.endWrite();

            AemsResponse response = null;

            AemsAPI.setUrl("http://aemsserver.ddns.net:8084/AEMSWebService/RestInf");
            try {
                response = AemsAPI.call0(insert, null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int httpCode = response.getResponseCode();
            System.out.println("---------------------- " + httpCode + " ---------------------------");
            if (httpCode != 200 && errorCount < 3) {
                errorCount++;
                System.out.println("-------------------------- " + httpCode + " ---------------------------");
                signNotifications(username, password, userIdString);
            } else if (errorCount > 2) {
                Log.w(TAG, "Notification-Key konnte nicht an den Server übermittelt werden");
            }

        }
    }


    public void onLoginFailed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Login fehlgeschlagen - Überprüfen Sie Ihre Logindaten", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String user = _inputUsername.getText().toString();
        String password = _passwordText.getText().toString();
        CheckBox checkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxRememberLogin);
        boolean checkBoxChecked = checkBoxRememberMe.isChecked();

        if (user.isEmpty()) {
            _inputUsername.setError("Geben Sie einen Benutzernamen ein");
            valid = false;
        } else {
            _inputUsername.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("Geben Sie ein Passwort ein");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        System.out.println("--------------------- Check Box Status:" + checkBoxChecked);
        if (checkBoxChecked == false){
            checkBoxRememberMe.setError("Bitte speichern Sie die Benutzerdaten");
            valid = false;
        }
        else
        {
            checkBoxRememberMe.setError(null);
        }

        return valid;
    }
}
