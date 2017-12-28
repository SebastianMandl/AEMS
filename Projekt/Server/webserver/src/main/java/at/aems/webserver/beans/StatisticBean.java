/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import at.aems.webserver.AemsAPI;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class StatisticBean {
    
    private String jsonString;
    
    public StatisticBean() {
        
    }
    
    @PostConstruct
    public void init() {
        //JsonArray statistics = AemsAPI.doPost("query").getAsJsonArray();
        jsonString = "[\n" +
"    {\n" +
"        \"statistic_id\": 123,\n" +
"        \"statistic_name\": \"Strom 1\",\n" +
"        \"annotation\": \"Stromstatistik für Gebäude XYZ (AT1234 und AT9284)\",\n" +
"        \"display_home\": true,\n" +
"        \"display_android\": false,\n" +
"        \"meters\": []   \n" +
"    },\n" +
"    {\n" +
"        \"statistic_id\": 1234,\n" +
"        \"statistic_name\": \"Gas 3\",\n" +
"        \"annotation\": \"Statistik sämtlicher Gaszähler der Region XYZ\",\n" +
"        \"display_home\": false,\n" +
"        \"display_android\": true,\n" +
"        \"meters\": []   \n" +
"    }\n" +
"]";
    }
    
    public String getJson() {
        return jsonString;
    }
}
