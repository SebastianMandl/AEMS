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
public class Report extends GraphQLObjectType {
    
    private static Report instance;
    
    private static final GraphQLFieldDefinition ID = Query.getFieldDefinition("id", AEMSDatabase.REPORTS, AEMSDatabase.Reports.ID, Scalars.GraphQLInt);
    private static final GraphQLFieldDefinition NAME = Query.getFieldDefinition("name", AEMSDatabase.REPORTS, AEMSDatabase.Reports.NAME, Scalars.GraphQLString);
    private static final GraphQLFieldDefinition ANNOTATION = Query.getFieldDefinition("annotation", AEMSDatabase.REPORTS, AEMSDatabase.Reports.ANNOTATION, Scalars.GraphQLString);
    private static final GraphQLFieldDefinition FROM_DATE = Query.getFieldDefinition("from_date", AEMSDatabase.REPORTS, AEMSDatabase.Reports.FROM_DATE, Scalars.GraphQLString);
    private static final GraphQLFieldDefinition TO_DATE = Query.getFieldDefinition("to_date", AEMSDatabase.REPORTS, AEMSDatabase.Reports.TO_DATE, Scalars.GraphQLString);
    private static final GraphQLFieldDefinition PERIOD = Query.getFieldDefinition("period", Period.getInstance());
    private static final GraphQLFieldDefinition USER = GraphQLFieldDefinition.newFieldDefinition().name("user").type(User.getInstance()).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            JSONObject returnObj = new JSONObject();
            returnObj.put("id", Query.execQuery(obj, AEMSDatabase.REPORTS, AEMSDatabase.Reports.USER));
            return returnObj.toString();
        }
    }).build();
    
    public Report(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static Report getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(USER);
        defs.add(PERIOD);
        defs.add(ID);
        defs.add(NAME);
        defs.add(ANNOTATION);
        defs.add(FROM_DATE);
        defs.add(TO_DATE);
        
        instance = new Report("report", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
