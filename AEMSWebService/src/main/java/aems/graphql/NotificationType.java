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
public class NotificationType extends GraphQLObjectType {
    
    private static NotificationType instance;
    
    private static final GraphQLFieldDefinition ID = Query.getFieldDefinition("id", AEMSDatabase.NOTIFICATION_TYPES, AEMSDatabase.NotificationTypes.ID, Scalars.GraphQLInt);
    private static final GraphQLFieldDefinition DISPLAY_NAME = Query.getFieldDefinition("display_name", AEMSDatabase.NOTIFICATION_TYPES, AEMSDatabase.NotificationTypes.DISPLAY_NAME, Scalars.GraphQLString);
    public NotificationType(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static NotificationType getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(ID);
        defs.add(DISPLAY_NAME);
        
        instance = new NotificationType("notification_type", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
