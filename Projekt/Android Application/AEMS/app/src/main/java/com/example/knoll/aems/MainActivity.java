package com.example.knoll.aems;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Output;
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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.example.knoll.aems.data.statistic.StatisticFetcher;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.Manifest;


public class MainActivity extends AppCompatActivity {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;



    /** RIESIGER KOMMENTAR ( 1 / 3)
     *
     * Referenzen in der MainActivity auf die einzelnen Tabs
     *
     */
    private ChartViewTab tab1;
    private ChartViewTab tab2;
    private ChartViewTab tab3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tab1 = new App_Tab_1();
        tab2 = new App_Tab_2();
        tab3 = new App_Tab_3();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
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


                switch(mViewPager.getCurrentItem()){
                    case 0: downloadStatistic(tab1); break;
                    case 1: downloadStatistic(tab2); break;
                    case 2: downloadStatistic(tab3); break;
                }

            }
        });

    }

    private void downloadStatistic(ChartViewTab tab) {
        boolean saveToSd = false;

        Chart chart = tab.getChart();
        Bitmap image = chart.getChartBitmap();
        String filename = tab.getStatisticTitle();

        if(saveToSd) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            File filePath = new File(Environment.getExternalStorageDirectory(), "/meinetollencharts/");
            filePath.mkdirs();

            File imageFile = new File(filePath, filename);
            OutputStream stream = null;
            try {
                stream = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.flush();
                stream.close();
                Toast.makeText(this, "Speichern erfolgreich", Toast.LENGTH_LONG).show();
            } catch(IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Speichern fehlgeschlagen", Toast.LENGTH_LONG).show();
            }
        } else {
            String url = MediaStore.Images.Media.insertImage(getContentResolver(), image, filename, "Hello");
            if(url == null) {
                makeToast("Speichern fehlgeschlagen.");
            } else {
                makeToast("Statistik wurde gespeichert.");
            }
        }
    }

    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            return true;
        } else if (id == R.id.action_logout){
            Toast.makeText(this, "Sie werden abgemeldet", Toast.LENGTH_LONG).show();
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
