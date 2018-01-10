package com.example.knoll.aems;

/**
 * Created by knoll on 03.10.2017.
 */

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.Bind;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

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

        String username = _inputUsername.getText().toString();
        String password = _passwordText.getText().toString();

        byte[] salt = generateSalt();
        String hash = doMakeHash(username, password, salt);

        String saltStrg = new String(salt, StandardCharsets.UTF_8);




        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 2000);
    }

    private byte[] generateSalt() {
        //Generate Salt
        Random value = new SecureRandom();
        byte[] salt = new byte[32];
        value.nextBytes(salt);

        return salt;
    }

    private String doMakeHash(String username, String password, byte[] salt) {

        MessageDigest md = null;

        String hashString = username + password + salt;
        String generatedHash = null;

        //Make Hash out of username, password and salt
        try {
            md = MessageDigest.getInstance("SHA-512");

            md.update(hashString.getBytes());

            byte[] bytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < bytes.length; i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 64).substring(1));
            }

            generatedHash = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return generatedHash;
    }



    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login fehlgeschlagen", Toast.LENGTH_LONG).show();

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
