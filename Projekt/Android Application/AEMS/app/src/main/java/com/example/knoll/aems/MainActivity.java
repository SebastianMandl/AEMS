package com.example.knoll.aems;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.Chart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.EncryptionType;

public class MainActivity extends AppCompatActivity {

    private static final String PREFERENCE_KEY = "AemsLoginPreferenceKey";
    private static final String PREFERENCE_KEY_STATISTICS = "AemsStatisticsPreferenceKey";
    SharedPreferences sharedPreferences;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private ChartViewTab tab1;
    private ChartViewTab tab2;
    private ChartViewTab tab3;

    private int notificationID = 0;

    String userIdString;
    String username;
    String password;

    int errorCount = 0;

    int numberStatisticElements = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        username = sharedPreferences.getString("USERNAME", null);
        System.out.println("Username:-------------------------" + username);
        password = sharedPreferences.getString("PASSWORD", null);
        System.out.println("Password:-------------------------" + password);

       if (username == null || username.equals("null") || username == "") {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
       }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadStatisticDataFromServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        tab1 = new App_Tab_1();
        tab2 = new App_Tab_2();
        tab3 = new App_Tab_3();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChartViewTab currentTab = mSectionsPagerAdapter.getCurrentTab();
                Snackbar.make(view, currentTab.getStatisticTitle() + " wird heruntergeladen!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        downloadStatistic(tab1);
                        break;
                    case 1:
                        downloadStatistic(tab2);
                        break;
                    case 2:
                        downloadStatistic(tab3);
                        break;
                }
            }
        });
    }

    private void loadStatisticDataFromServer() throws JSONException {

        sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        userIdString = sharedPreferences.getString("USERID", null);
        System.out.println("User Id:-------------------------" + userIdString);
        username = sharedPreferences.getString("USERNAME", null);
        System.out.println("Username:-------------------------" + username);
        password = sharedPreferences.getString("PASSWORD", null);
        System.out.println("Password:-------------------------" + password);


        if (userIdString == null || userIdString.equals("null") || userIdString == "") {
            sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        AemsUser user = new AemsUser(Integer.parseInt(userIdString), username, password);

        AemsQueryAction action = new AemsQueryAction(user, EncryptionType.SSL);
        action.setQuery("{statistics(user:\"" + userIdString + "\", display_android:\"true\"){id, name, period{name}}}");
        System.out.println("{statistics(user:\"" + userIdString + "\", display_android:\"true\"){id}}");

        AemsAPI.setTimeout(8000);
        AemsAPI.setUrl("http://aemsserver.ddns.net:8084/AEMSWebService/RestInf");
        AemsResponse response = null;
        try {
            response = AemsAPI.call0(action, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int httpCode = response.getResponseCode();
        System.out.println("------------------------------" + httpCode + "------------------------------------------------------");
        if (httpCode != 200 && errorCount < 3) {
            errorCount++;
            loadStatisticDataFromServer();
        } else if (errorCount > 2) {
            Log.w("", "Statistiken konnten nicht vom Server geladen werden");
        }
        System.out.println(response.getDecryptedResponse());

        JSONObject[] jsons = getDataFromJson(response.getDecryptedResponse());
        ArrayList<String> statisticIds = new ArrayList<>();
        ArrayList<String> periods = new ArrayList<>();
        ArrayList<String> statisticNames = new ArrayList<>();

        for (int i = 0; i < jsons.length; i++) {
            statisticIds.add(jsons[i].getString("id"));
            statisticNames.add(jsons[i].getString("name"));
            periods.add(jsons[i].getJSONObject("period").getString("name"));
        }
        numberStatisticElements = statisticIds.size();
        System.out.println("Number Elements:"+numberStatisticElements);
        System.out.println(statisticIds);
        System.out.println(statisticNames);
        System.out.println(periods);
        System.out.println();


        sharedPreferences = getSharedPreferences(PREFERENCE_KEY_STATISTICS, MODE_PRIVATE);
       // sharedPreferences.edit().clear().commit();
        if (statisticIds.size() == 1) {
            sharedPreferences.edit().putInt("STATISTIC1ID", Integer.parseInt(statisticIds.get(0))).commit();
            sharedPreferences.edit().putString("STATISTIC1PERIOD", periods.get(0)).commit();
            sharedPreferences.edit().putString("STATISTIC1NAME", statisticNames.get(0)).commit();
        } else if (statisticIds.size() == 2) {
            sharedPreferences.edit().putInt("STATISTIC1ID", Integer.parseInt(statisticIds.get(0))).commit();
            sharedPreferences.edit().putString("STATISTIC1PERIOD", periods.get(0)).commit();
            sharedPreferences.edit().putString("STATISTIC1NAME", statisticNames.get(0)).commit();

            sharedPreferences.edit().putInt("STATISTIC2ID", Integer.parseInt(statisticIds.get(1))).commit();
            sharedPreferences.edit().putString("STATISTIC2PERIOD", periods.get(1)).commit();
            sharedPreferences.edit().putString("STATISTIC2NAME", statisticNames.get(1)).commit();
        } else if (statisticIds.size() == 3) {
            sharedPreferences.edit().putInt("STATISTIC1ID", Integer.parseInt(statisticIds.get(0))).commit();
            sharedPreferences.edit().putString("STATISTIC1PERIOD", periods.get(0)).commit();
            sharedPreferences.edit().putString("STATISTIC1NAME", statisticNames.get(0)).commit();

            sharedPreferences.edit().putInt("STATISTIC2ID", Integer.parseInt(statisticIds.get(1))).commit();
            sharedPreferences.edit().putString("STATISTIC2PERIOD", periods.get(1)).commit();
            sharedPreferences.edit().putString("STATISTIC2NAME", statisticNames.get(1)).commit();

            sharedPreferences.edit().putInt("STATISTIC3ID", Integer.parseInt(statisticIds.get(2))).commit();
            sharedPreferences.edit().putString("STATISTIC3PERIOD", periods.get(2)).commit();
            sharedPreferences.edit().putString("STATISTIC3NAME", statisticNames.get(2)).commit();
        }

    }


    private JSONObject[] getDataFromJson(String decryptedResponse) throws JSONException {

        JSONObject json = new JSONObject(decryptedResponse);
        JSONArray jsonArray = json.getJSONArray("statistics");

        int size = jsonArray.length();
        ArrayList<JSONObject> arrays = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            JSONObject newJsonObject = jsonArray.getJSONObject(i);
            arrays.add(newJsonObject);
        }
        JSONObject[] myJsonObjectArray = new JSONObject[size];

        return arrays.toArray(myJsonObjectArray);
    }

    /**
     * javadoc
     *
     * @param tab
     */
    private void downloadStatistic(ChartViewTab tab) {
        boolean saveToSd = false;
        boolean canSaveImage = false;
        Chart chart = tab.getChart();
        Bitmap image = chart.getChartBitmap();
        String filename = tab.getStatisticTitle();
        File filePath = null;

        if (saveToSd) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            filePath = new File(Environment.getExternalStorageDirectory(), "/AEMS/");
            filePath.mkdirs();

            File imageFile = new File(filePath, filename + ".jpg");
            OutputStream stream = null;
            try {
                stream = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.flush();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (hasStoragePermission()) {
                String url = MediaStore.Images.Media.insertImage(getContentResolver(), image, filename, "Hello");
                if (url != null) {
                    canSaveImage = true;
                } else if (url == null) {
                    canSaveImage = false;
                }
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        12);
            }

        }
        doGenerateNotification(canSaveImage, filename);
    }


    private boolean hasStoragePermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void doGenerateNotification(boolean canSaveImage, String filename) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.logo_icon);

        if (canSaveImage) {
            builder.setContentTitle("Speichern erfolgreich");
            builder.setContentText("\"" + filename + "\" wurde erfolgreich gespeichert");
            notificationID++;
        } else {
            builder.setContentTitle("Speichern fehlgeschlagen");
            builder.setContentText("\"" + filename + "\" konnte nicht gespeichert werden");
            notificationID++;
        }

        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationID, builder.build());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_logout) {
            //Delete Logininformation
            sharedPreferences = this.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_showNotifications) {
            Intent intent = new Intent(this, AllNotifications.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                        return tab1;
                case 1:
                        return tab2;
                case 2:
                        return tab3;
                default:
                    return null;
            }
        }

        public ChartViewTab getCurrentTab() {
            return (ChartViewTab) getItem(mViewPager.getCurrentItem());
        }

        @Override
        public int getCount() {
            System.out.println("NumberStatisticElements");
          if(numberStatisticElements == 1){
                return 1;
            }
            else if(numberStatisticElements == 2){
                return 2;
            }
            else if(numberStatisticElements == 3){
                return 3;
            }
            else{
                // Show 3 total pages.
                return 3;
            }

            //return 3;

        }
    }

}
