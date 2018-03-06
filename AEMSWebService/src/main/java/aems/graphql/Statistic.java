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
public class Statistic extends GraphQLObjectType {
    
    private static Statistic instance;
    
    private static final GraphQLFieldDefinition PERIOD = Query.getFieldDefinition("period", Period.getInstance());
    private static final GraphQLFieldDefinition ANNOTATION = Query.getFieldDefinition("annotation", AEMSDatabase.STATISTICS, AEMSDatabase.Statistics.ANNOTATION, Scalars.GraphQLString);
    private static final GraphQLFieldDefinition DISPLAY_HOME = Query.getFieldDefinition("display_home", AEMSDatabase.STATISTICS, AEMSDatabase.Statistics.DISPLAY_HOME, Scalars.GraphQLBoolean);
    private static final GraphQLFieldDefinition DISPLAY_ANDROID = Query.getFieldDefinition("display_android", AEMSDatabase.STATISTICS, AEMSDatabase.Statistics.DISPLAY_ANDROID, Scalars.GraphQLBoolean);
    
    
    private static final GraphQLFieldDefinition ID = GraphQLFieldDefinition.newFieldDefinition().name("id").type(Scalars.GraphQLInt).dataFetcher(new DataFetcher<Integer> () {
        @Override
        public Integer get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return Integer.valueOf(Query.execQuery(obj, AEMSDatabase.STATISTICS, AEMSDatabase.Statistics.ID));
        }
    }).build();
    
    private static final GraphQLFieldDefinition NAME = GraphQLFieldDefinition.newFieldDefinition().name("name").type(Scalars.GraphQLString).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return Query.execQuery(obj, AEMSDatabase.STATISTICS, AEMSDatabase.Statistics.NAME);
        }
    }).build();
    
    private static final GraphQLFieldDefinition USER = GraphQLFieldDefinition.newFieldDefinition().name("user").type(User.getInstance()).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            JSONObject returnObj = new JSONObject();
            returnObj.put("id", Query.execQuery(obj, AEMSDatabase.STATISTICS, AEMSDatabase.Statistics.USER));
            return returnObj.toString();
        }
    }).build();
    
    public Statistic(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static Statistic getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(ID);
        defs.add(NAME);
        defs.add(USER);
        defs.add(DISPLAY_ANDROID);
        defs.add(DISPLAY_HOME);
        defs.add(PERIOD);
        defs.add(ANNOTATION);
        
        instance = new Statistic("statistic", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
}
