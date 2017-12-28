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
public class StatisticTime extends GraphQLObjectType {
    
    private static StatisticTime instance;
    
    private static final GraphQLFieldDefinition ID = GraphQLFieldDefinition.newFieldDefinition().name("id").type(Scalars.GraphQLInt).dataFetcher(new DataFetcher<Integer> () {
        @Override
        public Integer get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return obj.has("id") ? obj.getInt("id") : Integer.valueOf(Query.execQuery(obj, AEMSDatabase.STATISTIC_TIMES, AEMSDatabase.StatisticTimes.ID));
        }
    }).build();
    
    private static final GraphQLFieldDefinition PERIOD_VALUE_1 = GraphQLFieldDefinition.newFieldDefinition().name("period_value_1").type(Scalars.GraphQLInt).dataFetcher(new DataFetcher<Integer> () {
        @Override
        public Integer get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return obj.has("period_value_1") ? obj.getInt("period_value_1") : Integer.valueOf(Query.execQuery(obj, AEMSDatabase.STATISTIC_TIMES, AEMSDatabase.StatisticTimes.PERIOD_VALUE_1));
        }
    }).build();
    
    private static final GraphQLFieldDefinition PERIOD_VALUE_2 = GraphQLFieldDefinition.newFieldDefinition().name("period_value_2").type(Scalars.GraphQLInt).dataFetcher(new DataFetcher<Integer> () {
        @Override
        public Integer get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return obj.has("period_value_2") ? obj.getInt("period_value_2") : Integer.valueOf(Query.execQuery(obj, AEMSDatabase.STATISTIC_TIMES, AEMSDatabase.StatisticTimes.PERIOD_VALUE_2));
        }
    }).build();
    
    private static final GraphQLFieldDefinition FROM_DATE = GraphQLFieldDefinition.newFieldDefinition().name("from_date").type(Scalars.GraphQLString).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return obj.has("from_date") ? obj.getString("from_date") : Query.execQuery(obj, AEMSDatabase.STATISTIC_TIMES, AEMSDatabase.StatisticTimes.FROM_DATE);
        }
    }).build();
    
    private static final GraphQLFieldDefinition TO_DATE = GraphQLFieldDefinition.newFieldDefinition().name("to_date").type(Scalars.GraphQLString).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return obj.has("to_date") ? obj.getString("to_date") : Query.execQuery(obj, AEMSDatabase.STATISTIC_TIMES, AEMSDatabase.StatisticTimes.TO_DATE);
        }
    }).build();
    
    private static final GraphQLFieldDefinition PERIOD = GraphQLFieldDefinition.newFieldDefinition().name("period").type(Period.getInstance()).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            JSONObject returnObj = new JSONObject();
            returnObj.put("id", obj.get("period"));
            return returnObj.toString();
        }
    }).build();
    
    private static final GraphQLFieldDefinition STATISTIC = GraphQLFieldDefinition.newFieldDefinition().name("statistic").type(Statistic.getInstance()).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            JSONObject returnObj = new JSONObject();
            returnObj.put("id", obj.get("statistic"));
            return returnObj.toString();
        }
    }).build();
    
    public StatisticTime(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static StatisticTime getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(ID);
        defs.add(FROM_DATE);
        defs.add(TO_DATE);
        defs.add(PERIOD_VALUE_1);
        defs.add(PERIOD_VALUE_2);
        defs.add(STATISTIC);
        defs.add(PERIOD);
        
        instance = new StatisticTime("statistic_time", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
