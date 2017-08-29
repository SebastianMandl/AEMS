package com.example.knoll.aems;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by knoll on 26.08.2017.
 */

public class App_Tab_1 extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.app_tab_1, container, false);

        BarChart chart = (BarChart) view.findViewById(R.id.chart1);
        onCreateChart1(chart);

        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void onCreateChart1(BarChart chart){
        List<BarEntry> entries = new ArrayList<BarEntry>();
            entries.add(new BarEntry(1f, 2));
            entries.add(new BarEntry(2f, 1));
            entries.add(new BarEntry(4f, 15));
            entries.add(new BarEntry(5f, 13));
            entries.add(new BarEntry(7f, 4));
            entries.add(new BarEntry(8f, 5));
            entries.add(new BarEntry(10f, 7));
            entries.add(new BarEntry(11f, 8));


        BarDataSet dataSet = new BarDataSet(entries, "Stromausschlag");
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLUE);


        BarData barData = new BarData(dataSet);
        chart.setData(barData);
        chart.invalidate();
    }


}
