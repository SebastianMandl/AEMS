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
public class NotificationException extends GraphQLObjectType {
    
    private static NotificationException instance;
    
    private static final GraphQLFieldDefinition ID = Query.getFieldDefinition("id", AEMSDatabase.NOTIFICATION_EXCEPTIONS, AEMSDatabase.NotificationExceptions.ID, Scalars.GraphQLInt);
    private static final GraphQLFieldDefinition MAX_DEVIATION = Query.getFieldDefinition("max_deviation", AEMSDatabase.NOTIFICATION_EXCEPTIONS, AEMSDatabase.NotificationExceptions.MAX_DEVIATION, Scalars.GraphQLFloat);
    private static final GraphQLFieldDefinition NOTIFICATION = Query.getFieldDefinition("notification", Notification.getInstance());
    private static final GraphQLFieldDefinition PERIOD = Query.getFieldDefinition("period", Period.getInstance());
    private static final GraphQLFieldDefinition PERIOD_VALUE_FROM = Query.getFieldDefinition("period_value_from", AEMSDatabase.NOTIFICATION_EXCEPTIONS, AEMSDatabase.NotificationExceptions.PERIOD_VALUE_FROM, Scalars.GraphQLInt);
    private static final GraphQLFieldDefinition PERIOD_VALUE_TO = Query.getFieldDefinition("period_value_to", AEMSDatabase.NOTIFICATION_EXCEPTIONS, AEMSDatabase.NotificationExceptions.PERIOD_VALUE_TO, Scalars.GraphQLInt);
    private static final GraphQLFieldDefinition FROM_DATE = Query.getFieldDefinition("from_date", AEMSDatabase.NOTIFICATION_EXCEPTIONS, AEMSDatabase.NotificationExceptions.FROM_DATE, Scalars.GraphQLString);
    private static final GraphQLFieldDefinition TO_DATE = Query.getFieldDefinition("to_date", AEMSDatabase.NOTIFICATION_EXCEPTIONS, AEMSDatabase.NotificationExceptions.TO_DATE, Scalars.GraphQLString);
    
    
    public NotificationException(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static NotificationException getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(ID);
        defs.add(MAX_DEVIATION);
        defs.add(NOTIFICATION);
        defs.add(PERIOD);
        defs.add(PERIOD_VALUE_FROM);
        defs.add(PERIOD_VALUE_TO);
        defs.add(FROM_DATE);
        defs.add(TO_DATE);
        
        instance = new NotificationException("notification_exception", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
