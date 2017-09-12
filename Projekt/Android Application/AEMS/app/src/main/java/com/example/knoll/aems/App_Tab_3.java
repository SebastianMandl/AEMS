package com.example.knoll.aems;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.Chart;

/**
 * Created by knoll on 26.08.2017.
 */

public class App_Tab_3 extends ChartViewTab {

    public App_Tab_3() {
        super(R.layout.app_tab_3, R.id.chart3);
    }

    @Override
    public void onCreateChart(Chart chart) {
        // Dis not implemento yeto
    }

    @Override
    public String getStatisticTitle() {
        return "WasWeißIch1";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
