/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aems.graphql;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import at.htlgkr.aems.database.AEMSDatabase;
import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;

/**
 *
 * @author Sebastian
 */
public class Meter extends GraphQLObjectType {
    
    private static Meter instance;
    
    private static final GraphQLFieldDefinition ID = GraphQLFieldDefinition.newFieldDefinition().name("id").type(Scalars.GraphQLString).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return Query.execQuery(obj, AEMSDatabase.METERS, AEMSDatabase.Meters.ID);
        }
    }).build();
    
    private static final GraphQLFieldDefinition LATITUDE = GraphQLFieldDefinition.newFieldDefinition().name("latitude").type(Scalars.GraphQLFloat).dataFetcher(new DataFetcher<Double> () {
        @Override
        public Double get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return Double.valueOf(Query.execQuery(obj, AEMSDatabase.METERS, AEMSDatabase.Meters.LATITUDE));
        }
    }).build();
    
    private static final GraphQLFieldDefinition LONGITUDE = GraphQLFieldDefinition.newFieldDefinition().name("longitude").type(Scalars.GraphQLFloat).dataFetcher(new DataFetcher<Double> () {
        @Override
        public Double get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return Double.valueOf(Query.execQuery(obj, AEMSDatabase.METERS, AEMSDatabase.Meters.LONGITUDE));
        }
    }).build();
    
    private static final GraphQLFieldDefinition CITY = GraphQLFieldDefinition.newFieldDefinition().name("city").type(Scalars.GraphQLString).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return Query.execQuery(obj, AEMSDatabase.METERS, AEMSDatabase.Meters.CITY);
        }
    }).build();
    
    private static final GraphQLFieldDefinition METERTYPE = GraphQLFieldDefinition.newFieldDefinition().name("metertype").type(MeterType.getInstance()).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            JSONObject returnObj = new JSONObject();
            returnObj.put("id", Query.execQuery(obj, AEMSDatabase.METERS, AEMSDatabase.Meters.METERTYPE));
            return returnObj.toString();
        }
    }).build();
    
    private static final GraphQLFieldDefinition USER = GraphQLFieldDefinition.newFieldDefinition().name("user").type(User.getInstance()).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            JSONObject returnObj = new JSONObject();
            returnObj.put("id", obj.has("user") ? obj.getString("user") : obj.getString("id"));
            return returnObj.toString();
        }
    }).build();
    
    public Meter(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static Meter getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(ID);
        defs.add(USER);
        defs.add(CITY);
        defs.add(LATITUDE);
        defs.add(LONGITUDE);
        defs.add(METERTYPE);
        
        instance = new Meter("meter", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
