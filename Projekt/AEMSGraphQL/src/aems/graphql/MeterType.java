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
public class MeterType extends GraphQLObjectType {
    
    private static MeterType instance;
    
    public MeterType(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    private static final GraphQLFieldDefinition ID = GraphQLFieldDefinition.newFieldDefinition().name("id").type(Scalars.GraphQLInt).dataFetcher(new DataFetcher<Integer> () {
        @Override
        public Integer get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return Integer.valueOf(Query.execQuery(obj, AEMSDatabase.METERTYPES, AEMSDatabase.MeterTypes.ID));
        }
    }).build();
    
    private static final GraphQLFieldDefinition DISPLAY_NAME = GraphQLFieldDefinition.newFieldDefinition().name("name").type(Scalars.GraphQLString).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            return Query.execQuery(obj, AEMSDatabase.METERTYPES, AEMSDatabase.MeterTypes.DISPLAY_NAME);
        }
    }).build();
    
    
    public static MeterType getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(ID);
        defs.add(DISPLAY_NAME);
        
        instance = new MeterType("metertype", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
