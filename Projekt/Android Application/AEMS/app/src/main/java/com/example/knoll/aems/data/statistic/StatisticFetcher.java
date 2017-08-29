package com.example.knoll.aems.data.statistic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import at.htlgkr.aemsaccess.data.ApiData;
import at.htlgkr.aemsaccess.data.MultiItemFetcher;

/**
 * Created by Niklas on 29.08.2017.
 * This class is used to fetch the three pinned statistics of a user. Before fetching any
 * data, the {@link #setUserId(int)} function must be called.
 */
public class StatisticFetcher extends MultiItemFetcher {

    private int userId;

    public StatisticFetcher(String authString, String baseUrl) {
        super(authString, baseUrl);
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    @Override
    public List<Statistic> bulkFetch(Object... objects) {

        List<Statistic> result = new ArrayList<>();
        JSONArray statistics = fetchJson();
        for(int i = 0; i < statistics.length(); i++) {
            try {
                JSONObject statistic = statistics.getJSONObject(i);

                Integer sId = statistic.getInt("id");
                String sTitle = statistic.getString("title");

                String[]labels = json2string(statistic.getJSONArray("labels"));
                Double[]consumptions = json2double(statistic.getJSONArray("consumption"));
                Double[]prevConsumptions = null;
                Double[]temperature = null;
                Double[]prevTemperature = null;
                if(statistic.has("previousConsumption")) {
                    prevConsumptions = json2double(statistic.getJSONArray("previousConsumption"));
                }
                // Others

                Statistic stat = new Statistic(sId);
                stat.setTitle(sTitle);
                stat.setLabels(labels);
                stat.setConsumptionValues(consumptions);
                stat.setPrevConsumptionValues(prevConsumptions);
                stat.setTemperatureValues(temperature);
                stat.setPrevTemperatureValues(prevTemperature);

                result.add(stat);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return result;
    }

    private Double[] json2double(JSONArray source) throws JSONException {
        Double[]result = new Double[source.length()];
        for(int i = 0; i < result.length; i++) {
            result[i] = source.getDouble(i);
        }
        return result;
    }

    private String[] json2string(JSONArray source) throws JSONException{
        String[]result = new String[source.length()];
        for(int i = 0; i < result.length; i++) {
            result[i] = source.getString(i);
        }
        return result;
    }

    @Override
    public String getSubUrl() {
        // Url could look like this
        return "statistics/pinned/" + userId;
    }
}
