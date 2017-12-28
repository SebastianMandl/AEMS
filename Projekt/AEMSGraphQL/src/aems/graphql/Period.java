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
public class Period extends GraphQLObjectType{
    
    private static Period instance;
    
    private static final GraphQLFieldDefinition ID = GraphQLFieldDefinition.newFieldDefinition().name("id").type(Scalars.GraphQLString).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return obj.has("id") ? obj.getString("id") : Query.execQuery(obj, AEMSDatabase.PERIODS, AEMSDatabase.Periods.ID);
        }
    }).build();
    
    private static final GraphQLFieldDefinition NAME = GraphQLFieldDefinition.newFieldDefinition().name("name").type(Scalars.GraphQLString).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return obj.has("name") ? obj.getString("name") : Query.execQuery(obj, AEMSDatabase.PERIODS, AEMSDatabase.Periods.NAME);
        }
    }).build();
    
    public Period(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static Period getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(ID);
        defs.add(NAME);
        
        instance = new Period("period", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
