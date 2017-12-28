/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aems.graphql;

import java.util.ArrayList;
import java.util.List;

import at.htlgkr.aems.database.AEMSDatabase;
import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;

/**
 *
 * @author Sebastian
 */
public class WeatherData extends GraphQLObjectType {
    
    private static WeatherData instance;
    
    private static final GraphQLFieldDefinition ID = Query.getFieldDefinition("id", AEMSDatabase.WEATHERDATA, AEMSDatabase.WeatherData.ID, Scalars.GraphQLInt);
    private static final GraphQLFieldDefinition METER = Query.getFieldDefinition("meter", Meter.getInstance());
    private static final GraphQLFieldDefinition TIMESTAMP = Query.getFieldDefinition("timestamp", AEMSDatabase.WEATHERDATA, AEMSDatabase.WeatherData.TIMESTAMP, Scalars.GraphQLString);
    private static final GraphQLFieldDefinition TEMPERATURE = Query.getFieldDefinition("temperature", AEMSDatabase.WEATHERDATA, AEMSDatabase.WeatherData.TEMPERATURE, Scalars.GraphQLFloat);
    private static final GraphQLFieldDefinition HUMIDITY = Query.getFieldDefinition("humidity", AEMSDatabase.WEATHERDATA, AEMSDatabase.WeatherData.HUMIDITY, Scalars.GraphQLFloat);
    
    public WeatherData(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static WeatherData getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(ID);
        defs.add(METER);
        defs.add(TIMESTAMP);
        defs.add(TEMPERATURE);
        defs.add(HUMIDITY);
        
        instance = new WeatherData("weather_data", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
