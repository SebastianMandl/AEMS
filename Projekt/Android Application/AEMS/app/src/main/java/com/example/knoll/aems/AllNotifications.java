package com.example.knoll.aems;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by knoll on 25.11.2017.
 */

public class AllNotifications extends Activity {

    ListView notifications;
    ArrayList<Integer> idImages;
    ArrayList<String> titleList;
    ArrayList<String> infoList;
    ArrayList<String> notificationType;
    NotificationAdapter adapter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_notifications);


        loadData();

    }

    private void loadData() {

        notifications = (ListView) findViewById(R.id.listAllNotifications);
        idImages = new ArrayList<>();
        titleList = new ArrayList<>();
        infoList = new ArrayList<>();
        notificationType = new ArrayList<>();

        idImages = getImageList();
        titleList = getTitleList();
        infoList = getInfoList();
        notificationType = getNotificationTypleList();

        adapter = new NotificationAdapter(AllNotifications.this, titleList, infoList, notificationType, idImages);
        notifications.setAdapter(adapter);

        notifications.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AllNotifications.this, Notification.class);
                startActivity(intent);
            }
        });


    }


    public ArrayList<String> getTitleList(){
        titleList = new ArrayList<>();
        titleList.add("Stromzähler 1");
        titleList.add("Stromzähler 2");
        titleList.add("Gaszähler 3");
        titleList.add("Wärmemengenzähler 1");
        titleList.add("Wasserzähler 12");

        return titleList;
    }

    public ArrayList<Integer> getImageList(){
        idImages = new ArrayList<>();
        idImages.add(R.drawable.logo_icon);
        idImages.add(R.drawable.logo_icon);
        idImages.add(R.drawable.logo_icon);
        idImages.add(R.drawable.logo_icon);
        idImages.add(R.drawable.logo_icon);

        return idImages;
    }

    public ArrayList<String> getInfoList(){
        infoList = new ArrayList<>();
        infoList.add("Zähler braucht 20 % zu viel Strom");
        infoList.add("Zähler braucht 50 % zu viel Strom");
        infoList.add("Zähler braucht 60 % zu viel Strom");
        infoList.add("Zähler braucht 80 % zu viel Strom");
        infoList.add("Zähler braucht 30 % zu viel Strom");

        return infoList;
    }

    public ArrayList<String> getNotificationTypleList(){
        notificationType = new ArrayList<>();
        notificationType.add("Warnung");
        notificationType.add("Benachrichtigung");
        notificationType.add("Benachrichtigung");
        notificationType.add("Warnung");
        notificationType.add("Benachrichtigung");

        return notificationType;
    }


}
