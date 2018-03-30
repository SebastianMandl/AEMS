package at.aems.reportlib;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsStatisticAction;
import at.aems.apilib.AemsUser;

public class Statistic {
    private int id;
    private String title;
    private String annoation;
    private int periodId;
    private List<Double> consumptionValues = new ArrayList<>();
    private List<Double> previousValues = new ArrayList<>();
    private List<Double> anomalyValues = new ArrayList<>();
    
    public Statistic(int id) {
        this.id = id;
    }
    
    public void fetch(AemsUser user) {
        fetchMeta(user);
        fetchData(user);
    }

    private void fetchData(AemsUser user) {
        AemsStatisticAction statisticAction = new AemsStatisticAction(user);
        statisticAction.setStatisticId(this.id);
        AemsResponse respns = Utils.callApi(statisticAction);
        JsonArray period = respns.getAsJsonObject().get("period").getAsJsonArray();
        JsonArray prePeriod = respns.getAsJsonObject().get("pre_period").getAsJsonArray();
        for(JsonElement e : period) {
            this.consumptionValues.add((double) e.getAsDouble());
        }
        for(JsonElement e : prePeriod) {
            this.consumptionValues.add((double) e.getAsDouble());
        }
    }

    private void fetchMeta(AemsUser user) {
        String metaQuery = Utils.readContents("statisticmeta_query");
        metaQuery = Utils.addPlaceholders(metaQuery, this.id);
        AemsQueryAction query1 = new AemsQueryAction(user);
        query1.setQuery(metaQuery);
        AemsResponse resp = Utils.callApi(query1);
        JsonArray array1 = resp.getJsonArrayWithinObject();
        for(JsonElement e : array1) {
            this.title = e.getAsJsonObject().get("name").getAsString();
            this.annoation = e.getAsJsonObject().get("name").getAsString();
            this.periodId = e.getAsJsonObject().get("period").getAsJsonObject().get("id").getAsInt();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnnoation() {
        return annoation;
    }

    public void setAnnoation(String annoation) {
        this.annoation = annoation;
    }

    public List<Double> getConsumptionValues() {
        return consumptionValues;
    }

    public void setConsumptionValues(List<Double> consumptionValues) {
        this.consumptionValues = consumptionValues;
    }

    public List<Double> getPreviousValues() {
        return previousValues;
    }

    public void setPreviousValues(List<Double> previousValues) {
        this.previousValues = previousValues;
    }

    public List<Double> getAnomalyValues() {
        return anomalyValues;
    }

    public void setAnomalyValues(List<Double> anomalyValues) {
        this.anomalyValues = anomalyValues;
    }
    
    public int getPeriodId() {
        return periodId;
    }
    
    public void setPeriodId(int periodId) {
        this.periodId = periodId;
    }
    
    
}
