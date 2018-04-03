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
public class Responsibility extends GraphQLObjectType {
    
    private static Responsibility instance;
    
    private static final GraphQLFieldDefinition POSTAL_CODE = Query.getFieldDefinition("postal_code", AEMSDatabase.RESPONSIBILITIES, AEMSDatabase.Responsibilities.POSTAL_CODE, Scalars.GraphQLInt);
    private static final GraphQLFieldDefinition USER = Query.getFieldDefinition("user", User.getInstance());
    private static final GraphQLFieldDefinition DESIGNATION = Query.getFieldDefinition("designation", AEMSDatabase.RESPONSIBILITIES, AEMSDatabase.Responsibilities.DESIGNATION, Scalars.GraphQLString);

    public Responsibility(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static Responsibility getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(POSTAL_CODE);
        defs.add(USER);
        defs.add(DESIGNATION);
        
        instance = new Responsibility("responsibility", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
