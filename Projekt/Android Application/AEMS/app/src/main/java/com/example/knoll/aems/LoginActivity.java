package com.example.knoll.aems;

/**
 * Created by knoll on 03.10.2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsLoginAction;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.Decrypter;
import at.aems.apilib.crypto.EncryptionType;
//import at.htlgkr.aems.util.crypto.KeyUtils;
//import at.htlgkr.aems.util.key.DiffieHellmanProcedure;
import at.htlgkr.aems.util.crypto.KeyUtils;
import at.htlgkr.aems.util.key.DiffieHellmanProcedure;
import butterknife.ButterKnife;
import butterknife.Bind;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String PREFERENCE_KEY = "AemsLoginPreferenceKey";
    SharedPreferences sharedPreferences;
    byte[] sharedSecretKey;
    int httpCode = 0;
    String decryptedText;
    String userId = "";
    String username = "";
    String password = "";

    static volatile BigDecimal key = null;
    static volatile BigInteger keyInt = null;
    static volatile boolean wait = true;

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

        username = _inputUsername.getText().toString();
        password = _passwordText.getText().toString();

        loginToAEMS();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if(httpCode == 200){
                            System.out.println("-------------------------------onLoginSuccess()--------------------------------------------");
                            onLoginSuccess(username, password);
                        }
                        else{
                            onLoginFailed();
                        }
                        progressDialog.dismiss();
                    }
                }, 5000);
    }


    private void loginToAEMS() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String key = null;
                URL url = null;
                HttpURLConnection urlConnection = null;
                try {
                    //aemsserver.ddns.net:8084/AEMSWebService/AAA
                    //http://10.0.6.41:8084/AEMSWebService/AAA?android=android
                    //http://10.0.0.25:8084/AEMSWebService/AAA?android=android

<<<<<<< HEAD
                    // SPASSSSSSSSSSSSDDDDDDDDDD LUGGGGGIIIIIIIIIIII
                    // AemsAPI.setUrl("http:aemsserver.ddns.net:8084/AEMSWebService/RestInf");
                    AemsAPI.setUrl("http://aemsserver.ddns.net:8084/AEMSWebService/RestInf");
=======
                    AemsAPI.setUrl("http:aemsserver.ddns.net:8084/AEMSWebService/RestInf");
>>>>>>> a9b43593bb9d453c6e4418c6f9fe05e8a8eca6ef

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
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

        }).start();
    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(String email, String password) {
        _loginButton.setEnabled(true);
        System.out.println("-------------------------- We are in the onLoginSuccess() -------------------------------");
        //Save Logindata in SharedPreference
        sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        String user = sharedPreferences.getString("EMAIL", null);
        String passw = sharedPreferences.getString("PASSWORD", null);


        if(user == null && passw == null){
            System.out.println("----------------------------- user null und password null --------------------------------------");
            CheckBox checkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxRememberLogin);

                sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
                sharedPreferences.edit().putString("USERNAME", username).commit();
                sharedPreferences.edit().putString("PASSWORD", password).commit();
                sharedPreferences.edit().putBoolean("REMEMBERLOGIN", true).commit();
                sharedPreferences.edit().putString("USERID", userId).commit();
                sharedPreferences.edit().putBoolean("REMEMEBERME", checkBoxRememberMe.isChecked()).commit();
        }

        finish();
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
