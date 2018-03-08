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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;

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

        //Login to AEMS
        loginToAEMS();

        httpCode = 200;

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if(httpCode == 200){
                            onLoginSuccess(username, password);
                        }
                        else{
                            onLoginFailed();
                        }
                        progressDialog.dismiss();
                    }
                }, 2500);
    }

    private void loginToAEMS() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                try {
                    con = (HttpURLConnection) new URL("http://10.0.6.41:8084/AEMSWebService/AAA").openConnection();
                } catch (IOException e) {
                    System.out.println("---------------------------------HTTP URL Error-----------------------------------------");
                }
                try {
                    con.setRequestMethod("GET");
                } catch (ProtocolException e) {
                    System.out.println("---------------------------------Protokoll Error-----------------------------------------");
                    e.printStackTrace();
                }
                //con.setDoOutput(true);

                //con.setReadTimeout(1000);

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    for(String line = reader.readLine(); line != null; line = reader.readLine())
                        System.out.println(line);;

                } catch(Exception e) {
                    System.out.println("---------------------------------BufferedReaderError-----------------------------------------");
                }

                try {
                    System.out.println("----------------------------------------After Buffered Reader----------------------------------------");
                    DiffieHellmanProcedure.sendKeyInfos(new Socket(InetAddress.getByName("10.0.6.41"), 9950));
                    System.out.println("----------------------------------- After DiffieHellman---------------------------------------------");
                    key = KeyUtils.salt(new BigDecimal(new String(DiffieHellmanProcedure.confirmKey())), username, password);
                    System.out.println("----------------------------------- After Salt---------------------------------------------");
                } catch(Exception e) {
                    e.printStackTrace();
                }


                System.out.println("-----------------------------------------" + key + "------------------------------------------------------------");

                if(key != null)
                {
                    keyInt = key.unscaledValue();

                    sharedSecretKey = keyInt.toByteArray();

                    AemsAPI.setUrl("http://10.10.0.167:8084/AEMSWebService/RestInf");

                    AemsLoginAction loginAction = new AemsLoginAction(EncryptionType.AES);
                    loginAction.setUsername(username);
                    loginAction.setPassword(password);

                    AemsResponse response = null;

                    try {
                        response = AemsAPI.call0(loginAction, sharedSecretKey);
                        httpCode = response.getResponseCode();
                        decryptedText = response.getDecryptedResponse();

                        JSONObject json = new JSONObject(decryptedText);
                        userId = json.getString("id");

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

        //Save Logindata in SharedPreference
        sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        String user = sharedPreferences.getString("EMAIL", null);
        String passw = sharedPreferences.getString("PASSWORD", null);


        if(user == null && passw == null){
            CheckBox checkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxRememberLogin);

            if(checkBoxRememberMe.isChecked()){
                sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
                sharedPreferences.edit().putString("USERNAME", username).commit();
                sharedPreferences.edit().putString("PASSWORD", password).commit();
                sharedPreferences.edit().putBoolean("REMEMBERLOGIN", true).commit();
                String keyString = sharedSecretKey + "";
                sharedPreferences.edit().putString("SHAREDSECRETKEYSTRING", keyString).commit();
                sharedPreferences.edit().putString("USERID", userId).commit();
                sharedPreferences.getString("SHAREDSECRETKEYSTRING", null);
            }
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
