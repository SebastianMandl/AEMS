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
public class Registration extends GraphQLObjectType {
    
    private static Registration instance;
    
    private static final GraphQLFieldDefinition EMAIL = Query.getFieldDefinition("email", AEMSDatabase.REGISTRATIONS, AEMSDatabase.Registrations.EMAIL, Scalars.GraphQLString);
    private static final GraphQLFieldDefinition CONFIRM_CODE = Query.getFieldDefinition("confirm_code", AEMSDatabase.REGISTRATIONS, AEMSDatabase.Registrations.CONFIRM_CODE, Scalars.GraphQLString);
    private static final GraphQLFieldDefinition TIMESTAMP = Query.getFieldDefinition("timestamp", AEMSDatabase.REGISTRATIONS, AEMSDatabase.Registrations.TIMESTAMP, Scalars.GraphQLString);
    
    public Registration(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static Registration getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(EMAIL);
        defs.add(CONFIRM_CODE);
        defs.add(TIMESTAMP);
        
        instance = new Registration("registration", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
