package com.example.knoll.aems;

/**
 * Created by knoll on 03.10.2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsLoginAction;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.Decrypter;
import at.aems.apilib.crypto.EncryptionType;
import butterknife.ButterKnife;
import butterknife.Bind;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String PREFERENCE_KEY = "AemsLoginPreferenceKey";
    SharedPreferences sharedPreferences;

    @Bind(R.id.input_username) EditText _inputUsername;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

       /* sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        String user = sharedPreferences.getString("EMAIL", null);
        String passw = sharedPreferences.getString("PASSWORD", null);
        Boolean rememberMe = sharedPreferences.getBoolean("REMEMBERLOGIN", true);

        if (rememberMe && user != null && passw != null){
            onLoginSuccess();
        }*/

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

        final String email = _inputUsername.getText().toString();
        final String password = _passwordText.getText().toString();
        final boolean boolLogin = true;

        //Login to API
/*
        AemsUser user = new AemsUser(0, email, password);
        AemsQueryAction action = new AemsQueryAction(user, EncryptionType.AES);
        action.setQuery("authentication-query");
        String json = action.toJson(sharedSecretKey);

        AemsAPI.setUrl("https://api.aems.at");
        String response = AemsAPI.call(action, sharedSecretKey);
        String decryptor = Decrypter.requestDecryption(sharedSecretKey, response.getBytes());

        AemsLoginAction loginAction = new AemsLoginAction(EncryptionType.AES);
        loginAction.setUsername(email);
        loginAction.setPassword(password);
*/


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if(boolLogin){
                            onLoginSuccess(email, password);
                        }
                        else{
                            onLoginFailed();
                        }
                        progressDialog.dismiss();
                    }
                }, 2000);
    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(String email, String password) {
        _loginButton.setEnabled(true);

        //Save Logindata in SharedPreference
        sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        String user = sharedPreferences.getString("EMAIL", null);
        String passw = sharedPreferences.getString("PASSWORD", null);


        if(user == null && passw == null){
            CheckBox checkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxRememberLogin);
            if(checkBoxRememberMe.isChecked()){
                sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
                sharedPreferences.edit().putString("EMAIL", email);
                sharedPreferences.edit().putString("PASSWORD", password);
                sharedPreferences.edit().putBoolean("REMEMBERLOGIN", true);
                sharedPreferences.edit().commit();

                user = sharedPreferences.getString("EMAIL", "");
                passw = sharedPreferences.getString("PASSWORD", "");
                System.out.println("---------------------------------------------" + email + "-----" + password + "------------------------------------------------");
                System.out.println("---------------------------------------------" + user + "-----" + passw + "----------------------------------------------------");

            }

        }

        finish();
    }

    /*public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }*/

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login fehlgeschlagen - Überprüfen Sie Ihre Logindaten", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String user = _inputUsername.getText().toString();
        String password = _passwordText.getText().toString();


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

        return valid;
    }
}
