package com.example.knoll.aems.data.statistic;

import com.example.knoll.aems.data.TimeFrame;

import at.htlgkr.aemsaccess.data.ApiData;

/**
 * Created by Niklas on 29.08.2017.
 */

public class Statistic implements ApiData{
    private Integer id;
    private Integer ownerId;
    private String title;
    private TimeFrame timeFrame;

    private String[] labels;
    private Double[] consumptionValues;
    private Double[] prevConsumptionValues;
    private Double[] temperatureValues;
    private Double[] prevTemperatureValues;

    public Statistic(Integer statisticId) {
        this(statisticId, null, null);
    }

    public Statistic(Integer statisticId, Integer ownerId, String title) {
        this.id = statisticId;
        this.ownerId = ownerId;
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TimeFrame getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(TimeFrame timeFrame) {
        this.timeFrame = timeFrame;
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }

    public Double[] getConsumptionValues() {
        return consumptionValues;
    }

    public void setConsumptionValues(Double[] consumptionValues) {
        this.consumptionValues = consumptionValues;
    }

    public Double[] getPrevConsumptionValues() {
        return prevConsumptionValues;
    }

    public void setPrevConsumptionValues(Double[] prevConsumptionValues) {
        this.prevConsumptionValues = prevConsumptionValues;
    }

    public Double[] getTemperatureValues() {
        return temperatureValues;
    }

    public void setTemperatureValues(Double[] temperatureValues) {
        this.temperatureValues = temperatureValues;
    }

    public Double[] getPrevTemperatureValues() {
        return prevTemperatureValues;
    }

    public void setPrevTemperatureValues(Double[] prevTemperatureValues) {
        this.prevTemperatureValues = prevTemperatureValues;
    }
}
