package com.example.knoll.aems.data.statistic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOError;
import java.io.IOException;
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

    private Integer userId = null;

    public StatisticFetcher(String authString, String baseUrl) {
        super(authString, baseUrl);
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    /**
     * Returns a {@link List} of up to three statistics. These are the statistics that have been
     * pinned by the user. In the case of an error, {@code null} will be returned.
     * @param params Additional parameters if neccessary
     * @return A {@link List} of the users pinned statistics, or {@code null} if an error occured.
     */
    @Override
    public List<Statistic> bulkFetch(Object... params) {

        List<Statistic> result = new ArrayList<>();
        JSONArray statistics = null;
        try {
            statistics = fetchJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                return null;
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
        if(userId == null) {
            throw new RuntimeException("The userId cannot be null! Use the #setUserId function before fetching!");
        }
        return "statistics/pinned/" + userId;
    }
}
