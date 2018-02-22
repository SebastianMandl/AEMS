package com.example.knoll.aems;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

/**
 * Created by knoll on 25.11.2017.
 */

public class Notification extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notification);

        loadAllNotifications();
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
