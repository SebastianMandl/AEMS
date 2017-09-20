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
public class Notification extends GraphQLObjectType {
    
    private static Notification instance;
    
    private static final GraphQLFieldDefinition ID = Query.getFieldDefinition("id", AEMSDatabase.NOTIFICATIONS, AEMSDatabase.Notifications.ID, Scalars.GraphQLInt);
    private static final GraphQLFieldDefinition TYPE = Query.getFieldDefinition("type", AEMSDatabase.NOTIFICATIONS, AEMSDatabase.Notifications.TYPE, Scalars.GraphQLInt);
    private static final GraphQLFieldDefinition MAX_DEVIATION = Query.getFieldDefinition("max_deviation", AEMSDatabase.NOTIFICATIONS, AEMSDatabase.Notifications.MAX_DEVIATION, Scalars.GraphQLFloat);
    private static final GraphQLFieldDefinition NAME = Query.getFieldDefinition("name", AEMSDatabase.NOTIFICATIONS, AEMSDatabase.Notifications.NAME, Scalars.GraphQLString);
    private static final GraphQLFieldDefinition USER = GraphQLFieldDefinition.newFieldDefinition().name("user").type(User.getInstance()).dataFetcher(new DataFetcher<String> () {
        @Override
        public String get(DataFetchingEnvironment environment) {
            JSONObject obj = new JSONObject(environment.getSource().toString());
            JSONObject returnObj = new JSONObject();
            returnObj.put("id", Query.execQuery(obj, AEMSDatabase.NOTIFICATIONS, AEMSDatabase.Notifications.USER));
            return returnObj.toString();
        }
    }).build();
    
    public Notification(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static Notification getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(ID);
        defs.add(TYPE);
        defs.add(MAX_DEVIATION);
        defs.add(NAME);
        defs.add(USER);
        
        instance = new Notification("notification", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
