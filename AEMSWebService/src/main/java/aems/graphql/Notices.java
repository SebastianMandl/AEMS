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
public class Notices extends GraphQLObjectType {
    
    private static Notices instance;
    
    private static final GraphQLFieldDefinition ID = Query.getFieldDefinition("id", AEMSDatabase.NOTICES, AEMSDatabase.Notices.ID, Scalars.GraphQLInt);
    private static final GraphQLFieldDefinition METER = Query.getFieldDefinition("meter", Meter.getInstance());
    private static final GraphQLFieldDefinition SENSOR = Query.getFieldDefinition("sensor", Meter.getInstance());
    private static final GraphQLFieldDefinition NOTICE = Query.getFieldDefinition("notice", AEMSDatabase.NOTICES, AEMSDatabase.Notices.NOTICE, Scalars.GraphQLString);
    private static final GraphQLFieldDefinition SEEN = Query.getFieldDefinition("seen", AEMSDatabase.NOTICES, AEMSDatabase.Notices.SEEN, Scalars.GraphQLString);
    private static final GraphQLFieldDefinition TITLE = Query.getFieldDefinition("title", AEMSDatabase.NOTICES, AEMSDatabase.Notices.TITLE, Scalars.GraphQLString);
       
    public Notices(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static Notices getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(ID);
        defs.add(METER);
        defs.add(SENSOR);
        defs.add(NOTICE);
        defs.add(SEEN);
        defs.add(TITLE);
        
        instance = new Notices("notice", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
