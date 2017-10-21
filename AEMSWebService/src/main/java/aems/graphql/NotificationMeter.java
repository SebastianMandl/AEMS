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
public class NotificationMeter extends GraphQLObjectType {
    
    private static NotificationMeter instance;
    
    private static final GraphQLFieldDefinition METER = Query.getFieldDefinition("meter", Meter.getInstance());
    private static final GraphQLFieldDefinition NOTIFICATION = Query.getFieldDefinition("notification", Notification.getInstance());
    private static final GraphQLFieldDefinition POSITIVE_DEVIATION = Query.getFieldDefinition("positive_deviation", AEMSDatabase.NOTIFICATION_METERS, AEMSDatabase.NotificationMeters.POSITIVE_DEVIATION, Scalars.GraphQLBoolean);
    private static final GraphQLFieldDefinition NEGATIVE_DEVIATION = Query.getFieldDefinition("negative_deviation", AEMSDatabase.NOTIFICATION_METERS, AEMSDatabase.NotificationMeters.NEGATIVE_DEVIATION, Scalars.GraphQLBoolean);
    private static final GraphQLFieldDefinition POSITIVE_DEVIATION_VALUE = Query.getFieldDefinition("positive_deviation_value", AEMSDatabase.NOTIFICATION_METERS, AEMSDatabase.NotificationMeters.POSITIVE_DEVIATION_VALUE, Scalars.GraphQLFloat);
    private static final GraphQLFieldDefinition NEGATIVE_DEVIATION_VALUE = Query.getFieldDefinition("negative_deviation_value", AEMSDatabase.NOTIFICATION_METERS, AEMSDatabase.NotificationMeters.NEGATIVE_DEVIATION_VALUE, Scalars.GraphQLFloat);
    private static final GraphQLFieldDefinition PERIOD = Query.getFieldDefinition("period", Period.getInstance());
    
    private NotificationMeter(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }

    public static NotificationMeter getInstance() {  
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(METER);
        defs.add(NOTIFICATION);
        defs.add(POSITIVE_DEVIATION);
        defs.add(NEGATIVE_DEVIATION);
        defs.add(NEGATIVE_DEVIATION_VALUE);
        defs.add(POSITIVE_DEVIATION_VALUE);
        defs.add(PERIOD);
        
        instance = new NotificationMeter("notification_meter", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
