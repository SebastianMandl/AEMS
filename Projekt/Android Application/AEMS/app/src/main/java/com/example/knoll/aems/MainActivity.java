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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.Chart;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String PREFERENCE_KEY = "AemsLoginPreferenceKey";
    SharedPreferences sharedPreferences;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private ChartViewTab tab1;
    private ChartViewTab tab2;
    private ChartViewTab tab3;

    private int notificationID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        String user = sharedPreferences.getString("USERNAME", null);
        String passw = sharedPreferences.getString("PASSWORD", null);

        if (user == null && passw == null){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
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


    /**
     * javadoc
     * @param tab
     */
    private void downloadStatistic(ChartViewTab tab) {
        boolean saveToSd = false;
        boolean canSaveImage = false;
        Chart chart = tab.getChart();
        Bitmap image = chart.getChartBitmap();
        String filename = tab.getStatisticTitle();
        File filePath = null;

        if(saveToSd) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            filePath = new File(Environment.getExternalStorageDirectory(), "/AEMS/");
            filePath.mkdirs();

            File imageFile = new File(filePath, filename + ".jpg");
            OutputStream stream = null;
            try {
                stream = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.flush();
                stream.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else {
            if (hasStoragePermission()) {
                String url = MediaStore.Images.Media.insertImage(getContentResolver(), image, filename, "Hello");
                if(url != null){
                    canSaveImage = true;
                }
                else if(url == null) {
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

        if (canSaveImage){
            builder.setContentTitle("Speichern erfolgreich");
            builder.setContentText("\"" + filename + "\" wurde erfolgreich gespeichert");
            notificationID ++;
        }
        else
        {
            builder.setContentTitle("Speichern fehlgeschlagen");
            builder.setContentText("\"" + filename + "\" konnte nicht gespeichert werden");
            notificationID ++;
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
        if (id == R.id.action_logout){
            //Delete Logininformation
            sharedPreferences = this.getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        if(id == R.id.action_showNotifications){
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

            switch (position){
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
            // Show 3 total pages.
            return 3;
        }
    }
}
