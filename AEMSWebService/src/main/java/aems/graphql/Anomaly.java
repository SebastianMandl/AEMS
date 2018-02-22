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
public class Anomaly extends GraphQLObjectType {
    
    private static Anomaly instance;
    
    private static final GraphQLFieldDefinition ID = Query.getFieldDefinition("id", AEMSDatabase.ANOMALIES, AEMSDatabase.Anomalies.ID, Scalars.GraphQLInt);
    private static final GraphQLFieldDefinition METER = Query.getFieldDefinition("meter", Meter.getInstance());
    private static final GraphQLFieldDefinition SENSOR = Query.getFieldDefinition("sensor", Meter.getInstance());
    private static final GraphQLFieldDefinition SCRIPT = Query.getFieldDefinition("script", AEMSDatabase.ANOMALIES, AEMSDatabase.Anomalies.SCRIPT, Scalars.GraphQLString);
    private static final GraphQLFieldDefinition EXEC_INTERMEDIATE_TIME = Query.getFieldDefinition("exec_intermediate_time", AEMSDatabase.ANOMALIES, AEMSDatabase.Anomalies.EXEC_INTERMEDIATE_TIME, Scalars.GraphQLInt);
    private static final GraphQLFieldDefinition LAST_EXECUTION = Query.getFieldDefinition("last_execution", AEMSDatabase.ANOMALIES, AEMSDatabase.Anomalies.LAST_EXECUTION, Scalars.GraphQLString);
    private static final GraphQLFieldDefinition SCRIPT_ERRORS = Query.getFieldDefinition("script_errors", AEMSDatabase.ANOMALIES, AEMSDatabase.Anomalies.SCRIPT_ERRORS, Scalars.GraphQLString);
    public Anomaly(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static Anomaly getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(ID);
        defs.add(METER);
        defs.add(SENSOR);
        defs.add(SCRIPT);
        defs.add(LAST_EXECUTION);
        defs.add(EXEC_INTERMEDIATE_TIME);
        defs.add(SCRIPT_ERRORS);
        
        instance = new Anomaly("anomaly", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
