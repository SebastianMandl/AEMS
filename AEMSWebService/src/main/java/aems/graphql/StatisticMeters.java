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
public class StatisticMeters extends GraphQLObjectType {
    
//    statistic_id numeric(10,0) NOT NULL,
//  meter_id character varying(150) NOT NULL,
    
    private static StatisticMeters instance;
    
    private static final GraphQLFieldDefinition STATISTIC = GraphQLFieldDefinition.newFieldDefinition().name("statistic").type(Statistic.getInstance()).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            JSONObject returnObj = new JSONObject();
            returnObj.put("id", obj.get("statistic"));
            return returnObj.toString();
        }
    }).build();
    
    private static final GraphQLFieldDefinition METER = GraphQLFieldDefinition.newFieldDefinition().name("meter").type(Meter.getInstance()).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            JSONObject returnObj = new JSONObject();
            returnObj.put("id", obj.get("meter"));
            return returnObj.toString();
        }
    }).build();
    
    public StatisticMeters(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static StatisticMeters getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(STATISTIC);
        defs.add(METER);
        
        instance = new StatisticMeters("statistic_meter", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
}
