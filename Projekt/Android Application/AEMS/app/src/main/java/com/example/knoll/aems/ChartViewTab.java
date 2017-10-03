package com.example.knoll.aems;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by knoll on 06.09.2017.
 * This class represents a general chart fragment.
 */

public abstract class ChartViewTab extends android.support.v4.app.Fragment {

    protected Chart chart;
    protected int layoutId;
    protected int chartId;

    /**
     * Initializes the ChartViewTab
     * @param layoutId The {@code R.layout} id of the chart fragment
     * @param chartId The {@code R.id} id of the view that contains the chart
     */
    public ChartViewTab(int layoutId, int chartId) {
        super();
        this.layoutId = layoutId;
        this.chartId = chartId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId, container, false);

        Chart chart = (Chart) view.findViewById(chartId);
        this.chart = chart;
        onCreateChart(chart);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public abstract void onCreateChart(Chart chart);

    public Chart getChart() {
        return this.chart;
    }

    public abstract String getStatisticTitle();
}
