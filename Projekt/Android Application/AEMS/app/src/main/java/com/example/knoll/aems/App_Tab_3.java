package com.example.knoll.aems;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsStatisticAction;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.EncryptionType;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by knoll on 26.08.2017.
 */

public class App_Tab_3 extends ChartViewTab {

    public App_Tab_3() {
        super(R.layout.app_tab_3, R.id.chart3);
    }

    public String period = "Weekly";
    public boolean vorperiode = true;
    public boolean anomalie = true;
    String[] xVals;

    SharedPreferences sharedPreferences;
    private static final String PREFERENCE_KEY = "AemsLoginPreferenceKey";
    private static final String PREFERENCE_KEY_STATISTICS = "AemsStatisticsPreferenceKey";

    String username;
    String password;
    String userIdString;
    int statisticId;
    String statisticName;

    JsonArray periodValues;
    JsonArray prePeriodValues;
    JsonArray anomalyValues = new JsonArray();

    ArrayList<Float> floatValuesPeriod;
    ArrayList<Float> floatValuesPrePeriod;
    ArrayList<Float> floatValuesAnomaly;

    Float highestPeriodValue = 0f;
    Float highestAnomalyValue = 0f;


    @Override
    public void onCreateChart(Chart chart1) {
        loadDataFromSharedPreference();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                loadDataFromServer();
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        highestPeriodValue = getHighestValue(floatValuesPeriod);
        //highestAnomalyValue = getHighestValue(floatValuesAnomaly);


    CombinedChart chart = (CombinedChart)chart1;
        chart.setSaveEnabled(true);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.animateY(2000, Easing.EasingOption.EaseInBack);
        chart.setHighlightFullBarEnabled(false);
        chart.setTouchEnabled(false);

        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
        CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
    });

    //Legend Format
    Legend legend = chart.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);

        //Left Y-Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(highestPeriodValue *1.1f);


        //Anomalie Axis
        if(anomalie == true){
            //Right Y-Axis
            YAxis rightAxis = chart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setAxisMinimum(0f);
            rightAxis.setAxisMaximum(highestAnomalyValue);
        }



    //X-Axis
    XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        if(period.equals("Daily")){
        xVals = new String[]{"0-2 Uhr", "2-4 Uhr", "4-6 Uhr", "6-8 Uhr", "8-10 Uhr", "10-12 Uhr", "12-14 Uhr", "14-16 Uhr", "16-18 Uhr", "18-20 Uhr", "20-22 Uhr", "22-24 Uhr"};
        xAxis.setAxisMaximum(12f);
        xAxis.setLabelCount(12);
        xAxis.setLabelRotationAngle(25f);
        xAxis.setTextSize(7f);

        if(vorperiode == true){
            xAxis.setAxisMinimum(0.01f);
        }
        else {
            xAxis.setAxisMinimum(0.5f);
        }
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xVals[(int) value-1];
            }
        });
    }
        else if(period.equals("Weekly")){
        xVals = new String[]{"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"};
        xAxis.setAxisMaximum(7f);
        xAxis.setLabelCount(7);
        xAxis.setLabelRotationAngle(20f);
        xAxis.setTextSize(7f);

        if(vorperiode == true){
            xAxis.setAxisMinimum(0.01f);
        }
        else {
            xAxis.setAxisMinimum(0.5f);
        }
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xVals[(int) value-1];
            }
        });
    }
        else if(period.equals("Monthly")){
        xVals = new String[]{"Woche 1", "Woche 2", "Woche 3", "Woche 4"};
        xAxis.setAxisMaximum(4f);
        xAxis.setLabelCount(4);
        xAxis.setLabelRotationAngle(15f);
        xAxis.setTextSize(7f);

        if(vorperiode == true){
            xAxis.setAxisMinimum(0.01f);

            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return xVals[(int) value-1];
                }
            });
        }
        else {
            xAxis.setAxisMinimum(0.5f);
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return xVals[(int) value];
                }
            });
        }
    }
        else if(period.equals("Yearly")){
        xVals = new String[]{"Jänner", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};
        xAxis.setAxisMaximum(12f);
        xAxis.setLabelCount(12);
        xAxis.setLabelRotationAngle(25f);
        xAxis.setTextSize(7f);

        if(vorperiode == true){
            xAxis.setAxisMinimum(0.01f);
        }
        else {
            xAxis.setAxisMinimum(0.5f);
        }
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xVals[(int) value-1];
            }
        });
    }


        //Chart Object
        CombinedData data = new CombinedData();

        if (period.equals("Daily") && vorperiode == true && anomalie == true) {
            data.setData(generateBarDataDayWithPriorPeriod());
            data.setData(generateLineDataDay());
        } else if (period.equals("Daily") && vorperiode == true && anomalie == false) {
            data.setData(generateBarDataDayWithPriorPeriod());
        } else if (period.equals("Daily") && vorperiode == false && anomalie == true) {
            data.setData(generateBarDataDayWithoutPriorPeriod());
            data.setData(generateLineDataDaySingleBar());
        } else if (period.equals("Daily") && vorperiode == false && anomalie == false) {
            data.setData(generateBarDataDayWithoutPriorPeriod());
        } else if (period.equals("Weekly") && vorperiode == true && anomalie == true) {
            data.setData(generateBarDataWeekWithPriorPeriod());
            data.setData(generateLineDataWeek());
        } else if (period.equals("Weekly") && vorperiode == true && anomalie == false) {
            data.setData(generateBarDataWeekWithPriorPeriod());
        } else if (period.equals("Weekly") && vorperiode == false && anomalie == true) {
            data.setData(generateBarDataWeekWithoutPriorPeriod());
            data.setData(generateLineDataWeekSingleBar());
        } else if (period.equals("Weekly") && vorperiode == false && anomalie == false) {
            data.setData(generateBarDataWeekWithoutPriorPeriod());
        } else if (period.equals("Monthly") && vorperiode == true && anomalie == true) {
            data.setData(generateBarDataMonthWithPriorPeriod());
            data.setData(generateLineDataMonth());
        } else if (period.equals("Monthly") && vorperiode == true && anomalie == false) {
            data.setData(generateBarDataMonthWithPriorPeriod());
        } else if (period.equals("Monthly") && vorperiode == false && anomalie == true) {
            data.setData(generateBarDataMonthWithoutPriorPeriod());
            data.setData(generateLineDataMonthSingleBar());
        } else if (period.equals("Monthly") && vorperiode == false && anomalie == false) {
            data.setData(generateBarDataMonthWithoutPriorPeriod());
        } else if (period.equals("Yearly") && vorperiode == true && anomalie == true) {
            data.setData(generateBarDataYearWithPriorPeriod());
            data.setData(generateLineDataYear());
        } else if (period.equals("Yearly") && vorperiode == true && anomalie == false) {
            data.setData(generateBarDataYearWithPriorPeriod());
        } else if (period.equals("Yearly") && vorperiode == false && anomalie == true) {
            data.setData(generateBarDataYearWithoutPriorPeriod());
            data.setData(generateLineDataYearSingleBar());
        } else if (period.equals("Yearly") && vorperiode == false && anomalie == false) {
            data.setData(generateBarDataYearWithoutPriorPeriod());
        }


        xAxis.setAxisMaximum(data.getXMax() + 0.2f);

        chart.setData(data);
        chart.invalidate();
}

    private Float getHighestValue(ArrayList<Float> valueList) {
        Float highestValue = 0f;

        for(int i = 0; i<valueList.size(); i++){
            if(valueList.get(i)> highestValue){
                highestValue = valueList.get(i);
            }
        }
        return highestValue;
    }

    private void loadDataFromSharedPreference() {
        sharedPreferences = getContext().getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        username = sharedPreferences.getString("USERNAME", null);
        System.out.println("App_Tab_3: Username --------" + username);
        password = sharedPreferences.getString("PASSWORD", null);
        System.out.println("App_Tab_3: Password --------" + password);
        userIdString = sharedPreferences.getString("USERID", null);
        System.out.println("App_Tab_3: User Id --------" + userIdString);

        sharedPreferences = getContext().getSharedPreferences(PREFERENCE_KEY_STATISTICS, MODE_PRIVATE);
        statisticId = sharedPreferences.getInt("STATISTIC3ID", 0);
        period = sharedPreferences.getString("STATISTIC3PERIOD", null);
        statisticName = sharedPreferences.getString("STATISTIC3NAME", null);

    }

    private void loadDataFromServer() {

            AemsUser user = new AemsUser(Integer.parseInt(userIdString), username, password);
            AemsStatisticAction statisticAction = new AemsStatisticAction(user, EncryptionType.SSL);
            statisticAction.setStatisticId(statisticId);

            try {
                AemsAPI.setUrl("http://aemsserver.ddns.net:8084/AEMSWebService/RestInf");
                AemsResponse response = AemsAPI.call0(statisticAction, null);
                JsonObject json = response.getAsJsonObject();
                floatValuesPeriod = new ArrayList<>();
                periodValues = json.get("period").getAsJsonArray();
                if(periodValues != null){
                    System.out.println(periodValues);

                    for(int i = 0; i < periodValues.size(); i++){
                        floatValuesPeriod.add(periodValues.get(i).getAsFloat());
                    }
                }
                System.out.println("AppTab3: Float Values Period: -------------------------------" + floatValuesPeriod);


                floatValuesPrePeriod = new ArrayList<>();
                prePeriodValues = json.get("pre_period").getAsJsonArray();
                System.out.println(prePeriodValues);

                for(int i = 0; i < prePeriodValues.size(); i++){
                    floatValuesPrePeriod.add(periodValues.get(i).getAsFloat());
                }
                System.out.println("AppTab3: Float Values PrePeriod: -------------------------------" + floatValuesPrePeriod);


                floatValuesAnomaly = new ArrayList<>();
                //anomalyValues = json.get("anomalies").getAsJsonArray();
                System.out.println(anomalyValues);

            /*
            for(int i = 0; i < anomalyValues.size(); i++){
                floatValuesAnomaly.add(anomalyValues.get(i).getAsFloat());
            }
            System.out.println("AppTab2: Float Values PrePeriod: -------------------------------" + floatValuesPrePeriod);
*/

                if (prePeriodValues.size() == 0) {
                    vorperiode = false;
                    System.out.println("Preperiod is empty");
                } else {
                    vorperiode = true;
                }
                if (anomalyValues.size() == 0) {
                    System.out.println("Anomaly is empty");
                    anomalie = false;
                } else {
                    anomalie = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    @Override
    public String getStatisticTitle() {
        return null;
    }


    private BarData loadGoupedDataSet(ArrayList<BarEntry> entries1, ArrayList<BarEntry> entries2) {
        BarDataSet set1 = new BarDataSet(entries1, "Aktueller Verbrauch");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(5f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);


        BarDataSet set2 = new BarDataSet(entries2, "Verbrauch Vorperiode");
        set2.setColor(Color.rgb(61, 165, 255));
        set2.setValueTextColor(Color.rgb(61, 165, 255));
        set2.setValueTextSize(5f);

        float groupSpace = 0.15f; // Space between Bar Groups
        float barSpace = 0.05f;
        float barWidth = 0.385f;

        BarData barD = new BarData(set1, set2);
        barD.setBarWidth(barWidth);

        // Makes BarData object grouped
        barD.groupBars(0, groupSpace, barSpace); // Start at x = 0

        return barD;
    }

    private BarData loadSingleBarDataSet(ArrayList<BarEntry> entries1) {
        BarDataSet set1 = new BarDataSet(entries1, "Aktueller Verbrauch");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(8f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);

        float barWidth = 0.5f;

        BarData barD = new BarData(set1);
        barD.setBarWidth(barWidth);

        return barD;
    }

    private LineData loadLineData(ArrayList<Entry> entries){

        LineDataSet dataSet = new LineDataSet(entries, "Außentemperatur");

        dataSet.setColor(Color.LTGRAY);
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleColor(Color.BLACK);
        dataSet.setCircleRadius(3f);
        dataSet.setFillColor(Color.BLUE);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        dataSet.setDrawValues(false);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        LineData lineD = new LineData(dataSet);

        return lineD;
    }



    //Day
    private BarData generateBarDataDayWithPriorPeriod() {

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();

        for (int i = 0; i < 12; i++){
            if(floatValuesPeriod.size() > i){
                entries1.add(new BarEntry(i+1f, floatValuesPeriod.get(i)));
            }
            else
            {
                entries1.add(new BarEntry(i+1f, 0f));
            }
        }
        for (int i = 0; i < 12; i++){
            if(floatValuesPrePeriod.size() > i){
                entries2.add(new BarEntry(i+1f, floatValuesPrePeriod.get(i)));
            }
            else
            {
                entries2.add(new BarEntry(i+1f, 0f));
            }
        }

        BarData barD = loadGoupedDataSet(entries1, entries2);

        return barD;
    }


    private BarData generateBarDataDayWithoutPriorPeriod() {

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();

        for (int i = 0; i < 12; i++){
            if(floatValuesPeriod.size() > i){
                entries1.add(new BarEntry(i+1f, floatValuesPeriod.get(i)));
            }
            else
            {
                entries1.add(new BarEntry(i+1f, 0f));
            }
        }

        BarData barD = loadSingleBarDataSet(entries1);

        return barD;
    }


    //Line Data
    private LineData generateLineDataDay() {

        ArrayList<Entry> entries = new ArrayList<Entry>();
        ArrayList<Float> positions = new ArrayList<>();
        positions.add(0.6f);
        positions.add(1.6f);
        positions.add(2.6f);
        positions.add(3.6f);
        positions.add(4.6f);
        positions.add(5.6f);
        positions.add(6.65f);
        positions.add(7.65f);
        positions.add(8.65f);
        positions.add(9.65f);
        positions.add(10.65f);
        positions.add(11.65f);

        for (int i = 0; i < 12; i++){
            if(floatValuesAnomaly.size() > i){
                entries.add(new Entry(positions.get(i), floatValuesAnomaly.get(i)));
            }
            else
            {
                entries.add(new BarEntry(positions.get(i), 0f));
            }
        }

        LineData lineD = loadLineData(entries);

        return lineD;
    }

    private LineData generateLineDataDaySingleBar() {

        ArrayList<Entry> entries = new ArrayList<Entry>();

        for (int i = 0; i < 12; i++){
            if(floatValuesAnomaly.size() > i){
                entries.add(new Entry(i+1f, floatValuesAnomaly.get(i)));
            }
            else
            {
                entries.add(new BarEntry(i+1f, 0f));
            }
        }

        LineData lineD = loadLineData(entries);

        return lineD;
    }


    //Week
    private BarData generateBarDataWeekWithPriorPeriod() {

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();

        for (int i = 0; i < 7; i++){
            if(floatValuesPeriod.size() > i){
                entries1.add(new BarEntry(i+1f, floatValuesPeriod.get(i)));
            }
            else
            {
                entries1.add(new BarEntry(i+1f, 0f));
            }
        }
        for (int i = 0; i < 7; i++){
            if(floatValuesPrePeriod.size() > i){
                entries2.add(new BarEntry(i+1f, floatValuesPrePeriod.get(i)));
            }
            else
            {
                entries2.add(new BarEntry(i+1f, 0f));
            }
        }

        BarData barD = loadGoupedDataSet(entries1, entries2);

        return barD;
    }

    private BarData generateBarDataWeekWithoutPriorPeriod() {

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();

        for (int i = 0; i < 7; i++){
            if(floatValuesPeriod.size() > i){
                entries1.add(new BarEntry(i+1f, floatValuesPeriod.get(i)));
            }
            else
            {
                entries1.add(new BarEntry(i+1f, 0f));
            }
        }

        BarData barD = loadSingleBarDataSet(entries1);

        return barD;
    }

    //Line Data
    private LineData generateLineDataWeek() {

        ArrayList<Entry> entries = new ArrayList<Entry>();
        float position = 0.6f;

        for (int i = 0; i < 12; i++){
            if(floatValuesAnomaly.size() > i){
                entries.add(new Entry(i+position, floatValuesAnomaly.get(i)));
            }
            else
            {
                entries.add(new BarEntry(i+position, 0f));
            }
        }

        LineData lineD = loadLineData(entries);

        return lineD;
    }

    private LineData generateLineDataWeekSingleBar() {

        ArrayList<Entry> entries = new ArrayList<Entry>();
        float position = 1f;

        for (int i = 0; i < 12; i++){
            if(floatValuesAnomaly.size() > i){
                entries.add(new Entry(i+position, floatValuesAnomaly.get(i)));
            }
            else
            {
                entries.add(new BarEntry(i+position, 0f));
            }
        }

        LineData lineD = loadLineData(entries);

        return lineD;
    }


    //Month
    private BarData generateBarDataMonthWithPriorPeriod() {

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();

        for (int i = 0; i < 4; i++){
            if(floatValuesPeriod.size() > i){
                entries1.add(new BarEntry(i+1f, floatValuesPeriod.get(i)));
            }
            else
            {
                entries1.add(new BarEntry(i+1f, 0f));
            }
        }
        for (int i = 0; i < 4; i++){
            if(floatValuesPrePeriod.size() > i){
                entries2.add(new BarEntry(i+1f, floatValuesPrePeriod.get(i)));
            }
            else
            {
                entries2.add(new BarEntry(i+1f, 0f));
            }
        }

        BarData barD = loadGoupedDataSet(entries1, entries2);

        return barD;
    }


    private BarData generateBarDataMonthWithoutPriorPeriod() {

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();

        for (int i = 0; i < 4; i++){
            if(floatValuesPeriod.size() > i){
                entries1.add(new BarEntry(i+1f, floatValuesPeriod.get(i)));
            }
            else
            {
                entries1.add(new BarEntry(i+1f, 0f));
            }
        }

        BarData barD = loadSingleBarDataSet(entries1);

        return barD;
    }

    //Line Data
    private LineData generateLineDataMonth() {

        ArrayList<Entry> entries = new ArrayList<Entry>();
        float position = 0.6f;

        for (int i = 0; i < 12; i++){
            if(floatValuesAnomaly.size() > i){
                entries.add(new Entry(i+position, floatValuesAnomaly.get(i)));
            }
            else
            {
                entries.add(new BarEntry(i+position, 0f));
            }
        }

        LineData lineD = loadLineData(entries);

        return lineD;
    }

    private LineData generateLineDataMonthSingleBar() {

        ArrayList<Entry> entries = new ArrayList<Entry>();
        float position = 1f;

        for (int i = 0; i < 12; i++){
            if(floatValuesAnomaly.size() > i){
                entries.add(new Entry(i+position, floatValuesAnomaly.get(i)));
            }
            else
            {
                entries.add(new BarEntry(i+position, 0f));
            }
        }

        LineData lineD = loadLineData(entries);

        return lineD;
    }


    //Year
    private BarData generateBarDataYearWithPriorPeriod() {

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();

        for (int i = 0; i < 12; i++){
            if(floatValuesPeriod.size() > i){
                entries1.add(new BarEntry(i+1f, floatValuesPeriod.get(i)));
            }
            else
            {
                entries1.add(new BarEntry(i+1f, 0f));
            }
        }
        for (int i = 0; i < 12; i++){
            if(floatValuesPrePeriod.size() > i){
                entries2.add(new BarEntry(i+1f, floatValuesPrePeriod.get(i)));
            }
            else
            {
                entries2.add(new BarEntry(i+1f, 0f));
            }
        }

        BarData barD = loadGoupedDataSet(entries1, entries2);

        return barD;
    }

    private BarData generateBarDataYearWithoutPriorPeriod() {
        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();

        for (int i = 0; i < 12; i++){
            if(floatValuesPeriod.size() > i){
                entries1.add(new BarEntry(i+1f, floatValuesPeriod.get(i)));
            }
            else
            {
                entries1.add(new BarEntry(i+1f, 0f));
            }
        }

        BarData barD = loadSingleBarDataSet(entries1);

        return barD;
    }

    //Line Data
    private LineData generateLineDataYear() {

        ArrayList<Entry> entries = new ArrayList<Entry>();

        ArrayList<Float> positions = new ArrayList<>();
        positions.add(0.6f);
        positions.add(1.6f);
        positions.add(2.6f);
        positions.add(3.6f);
        positions.add(4.6f);
        positions.add(5.6f);
        positions.add(6.65f);
        positions.add(7.65f);
        positions.add(8.65f);
        positions.add(9.65f);
        positions.add(10.65f);
        positions.add(11.65f);

        for (int i = 0; i < 12; i++){
            if(floatValuesAnomaly.size() > i){
                entries.add(new Entry(positions.get(i), floatValuesAnomaly.get(i)));
            }
            else
            {
                entries.add(new BarEntry(positions.get(i), 0f));
            }
        }

        LineData lineD = loadLineData(entries);

        return lineD;
    }

    private LineData generateLineDataYearSingleBar() {

        ArrayList<Entry> entries = new ArrayList<Entry>();
        float position = 1f;

        for (int i = 0; i < floatValuesAnomaly.size(); i++) {
            entries.add(new BarEntry(i + position, floatValuesAnomaly.get(i)));
        }

        LineData lineD = loadLineData(entries);

        return lineD;
    }
}