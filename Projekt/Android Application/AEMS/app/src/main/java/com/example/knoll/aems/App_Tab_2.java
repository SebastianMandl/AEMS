package com.example.knoll.aems;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.github.mikephil.charting.charts.CombinedChart;
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
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import java.util.ArrayList;

/**
 * Created by knoll on 26.08.2017.
 */

public class App_Tab_2 extends Fragment {

    public CombinedChart chart;
    public TextView statisticTitle;

    public TextView getStatisticTitle() {
        return statisticTitle;
    }

    public CombinedChart getChart() {
        return chart;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_tab_2, container, false);


        chart = (CombinedChart) view.findViewById(R.id.combChart);
        statisticTitle = (TextView) view.findViewById(R.id.statisticTitle);

        createChart(chart);

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void createChart(CombinedChart chart) {

        chart.setSaveEnabled(true);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);


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
        leftAxis.setAxisMaximum(8f);

        //Right Y-Axis
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(15f);
        rightAxis.setAxisMaximum(30f);

        //X-Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(15f);
        xAxis.setLabelCount(5);
        xAxis.setDrawGridLines(false);


        CombinedData data = new CombinedData();

        data.setData(generateBarData());
        data.setData(generateLineData());


        xAxis.setAxisMaximum(data.getXMax() + 0.2f);

        chart.setData(data);
        chart.invalidate();

    }

    private BarData generateBarData() {

        ArrayList<BarEntry> entries1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> entries2 = new ArrayList<BarEntry>();


        entries1.add(new BarEntry(1f, 2));
        entries1.add(new BarEntry(1f, 6));
        entries1.add(new BarEntry(1f, 4));
        entries1.add(new BarEntry(1f, 7));
        entries1.add(new BarEntry(1f, 3));
        entries1.add(new BarEntry(1f, 7));
        entries1.add(new BarEntry(1f, 5));

        entries2.add(new BarEntry(1f, 2));
        entries2.add(new BarEntry(1f, 6));
        entries2.add(new BarEntry(1f, 5));
        entries2.add(new BarEntry(1f, 4));
        entries2.add(new BarEntry(1f, 3));
        entries2.add(new BarEntry(1f, 1));
        entries2.add(new BarEntry(1f, 5));



        BarDataSet set1 = new BarDataSet(entries1, "Aktuelle Werte");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(8f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);


        BarDataSet set2 = new BarDataSet(entries2, "Vorperiode");

        set2.setColor(Color.rgb(61, 165, 255));
        set2.setValueTextColor(Color.rgb(61, 165, 255));
        set2.setValueTextSize(8f);

        float groupSpace = 0.5f; // Space between Bar Groups
        float barSpace = 0.1f;
        float barWidth = 0.7f;

        BarData barD = new BarData(set1, set2);
        barD.setBarWidth(barWidth);

        // Makes BarData object grouped
        barD.groupBars(0, groupSpace, barSpace); // Start at x = 0

        return barD;
    }


    private LineData generateLineData() {

        LineData lineD = new LineData();

        ArrayList<Entry> entries = new ArrayList<Entry>();

        entries.add(new Entry(1f, 21));
        entries.add(new Entry(3f, 23));
        entries.add(new Entry(5f, 24));
        entries.add(new Entry(7f, 26));
        entries.add(new Entry(9f, 25));
        entries.add(new Entry(11f, 21));
        entries.add(new Entry(13f, 23));
        entries.add(new Entry(14.5f, 25));


        LineDataSet dataSet = new LineDataSet(entries, "Außentemperatur");
        dataSet.setColor(Color.rgb(240, 140, 70));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleColor(Color.rgb(240, 238, 70));
        dataSet.setCircleRadius(5f);
        dataSet.setFillColor(Color.BLUE);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawValues(false);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        lineD.addDataSet(dataSet);

        return lineD;
    }



}