package com.example.knoll.aems;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by knoll on 25.11.2017.
 */

public class Notification extends Activity {

    ArrayList<String> notificationItem;

    int errorCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        getData();
                        setData();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();

        loadAllNotifications();
    }

    private void setData() {
        TextView title = (TextView) findViewById(R.id.textViewZaehlerName);
        TextView type = (TextView) findViewById(R.id.textViewNotificationType);
        TextView reason = (TextView) findViewById(R.id.textViewReasonForNotification);
        TextView info = (TextView) findViewById(R.id.textViewFurtherInformation);
        TextView meterNumber = (TextView) findViewById(R.id.textViewZaehlerNumber);
        TextView sensor = (TextView) findViewById(R.id.textViewSensor);


        title.setText(notificationItem.get(1));
        type.setText(notificationItem.get(5));
        meterNumber.setText(notificationItem.get(2));
        reason.setText("Zähler " + notificationItem.get(2) + " meldet einen abweichenden Energieverbrauch");
        if(notificationItem.get(3).equals("null")){
            info.setText("Es wurde keine zusätzliche Information konfiguriert");
        }
        else{
            info.setText(notificationItem.get(3));
        }
        if(notificationItem.get(4).equals("null")){
            sensor.setText("Es wurde kein Sensor definiert");
        }
        else{
            sensor.setText(notificationItem.get(4));
        }
    }

    private void getData() {
        notificationItem = new ArrayList<>();

        Intent intent = getIntent();
        notificationItem = intent.getStringArrayListExtra("notificationItem");
        System.out.println(notificationItem);
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
