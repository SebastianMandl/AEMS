/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aems.graphql;

import aems.database.AEMSDatabase;
import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author Sebastian
 */
public class MeterData extends GraphQLObjectType {
    
    private static MeterData instance;
    
    private static final GraphQLFieldDefinition ID = GraphQLFieldDefinition.newFieldDefinition().name("id").type(Scalars.GraphQLInt).dataFetcher(new DataFetcher<Integer> () {
        @Override
        public Integer get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return Integer.valueOf(Query.execQuery(obj, AEMSDatabase.METERDATA, AEMSDatabase.MeterData.ID.name()));
        }
    }).build();
    
    private static final GraphQLFieldDefinition MEASURE_VALUE = GraphQLFieldDefinition.newFieldDefinition().name("value").type(Scalars.GraphQLFloat).dataFetcher(new DataFetcher<Double> () {
        @Override
        public Double get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return Double.valueOf(Query.execQuery(obj, AEMSDatabase.METERDATA, AEMSDatabase.MeterData.MEASURED_VALUE.name()));
        }
    }).build();
    
    private static final GraphQLFieldDefinition TIMESTAMP = GraphQLFieldDefinition.newFieldDefinition().name("timestamp").type(Scalars.GraphQLString).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return Query.execQuery(obj, AEMSDatabase.METERDATA, AEMSDatabase.MeterData.TIMESTAMP.name());
        }
    }).build();
    
    private static final GraphQLFieldDefinition METER = GraphQLFieldDefinition.newFieldDefinition().name("meter").type(Meter.getInstance()).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            JSONObject returnObj = new JSONObject();
            returnObj.put("id", obj.getString("meter"));
            return returnObj.toString();
        }
    }).build();
    
    
    public MeterData(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static MeterData getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(ID);
        defs.add(MEASURE_VALUE);
        defs.add(TIMESTAMP);
        defs.add(METER);
        
        instance = new MeterData("meterdata", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
