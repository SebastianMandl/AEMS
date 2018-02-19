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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsLoginAction;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.Decrypter;
import at.aems.apilib.crypto.EncryptionType;
import at.htlgkr.aems.util.crypto.KeyUtils;
import at.htlgkr.aems.util.key.DiffieHellmanProcedure;
import butterknife.ButterKnife;
import butterknife.Bind;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String PREFERENCE_KEY = "AemsLoginPreferenceKey";
    private static final String PREFERENCE_KEY_SESSION = "AemsLoginPreferenceKeySession";
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesSession;
    int httpCode = 0;
    String responseText = "";
    String responseDecryptedText = "";

    @Bind(R.id.input_username) EditText _inputUsername;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;

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

        final String email = _inputUsername.getText().toString();
        final String password = _passwordText.getText().toString();

        //Login to AEMS
        BigDecimal key = null;
        try {
            DiffieHellmanProcedure.sendKeyInfos(new Socket(InetAddress.getByName("localhost"), 9950));
            key = KeyUtils.salt(new BigDecimal(new String(DiffieHellmanProcedure.confirmKey())), email, password);
        } catch(Exception e) {
            e.printStackTrace();
        }

        BigInteger keyInt = key.unscaledValue();
        byte[] sharedSecretKey = keyInt.toByteArray();

        AemsAPI.setUrl("https://api.aems.at");

        AemsLoginAction loginAction = new AemsLoginAction(EncryptionType.AES);
        loginAction.setUsername(email);
        loginAction.setPassword(password);

        AemsResponse response = null;
        try {
            response = AemsAPI.call0(loginAction, sharedSecretKey);
            httpCode = response.getResponseCode();
            responseText = response.getResponseText();
            responseDecryptedText = response.getDecryptedResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if(httpCode == 200){
                            onLoginSuccess(email, password);
                        }
                        else{
                            onLoginFailed();
                        }
                        progressDialog.dismiss();
                    }
                }, 500);
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
                sharedPreferences.edit().putString("EMAIL", email).commit();
                sharedPreferences.edit().putString("PASSWORD", password).commit();
                sharedPreferences.edit().putBoolean("REMEMBERLOGIN", true).commit();
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
