/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aems.graphql;

import aems.database.AEMSDatabase;
import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sebastian
 */
public class NotificationTokens extends GraphQLObjectType {
    
    private static NotificationTokens instance;
    
    private static final GraphQLFieldDefinition TOKEN = Query.getFieldDefinition("token", AEMSDatabase.NOTIFICATION_TOKENS, AEMSDatabase.NotificationTokens.TOKEN, Scalars.GraphQLString);
    private static final GraphQLFieldDefinition USER = Query.getFieldDefinition("user", User.getInstance());
    
    public NotificationTokens(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static NotificationTokens getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(TOKEN);
        defs.add(USER);
        
        instance = new NotificationTokens("notification_token", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
