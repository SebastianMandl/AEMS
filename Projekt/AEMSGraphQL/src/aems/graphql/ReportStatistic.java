/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aems.graphql;

import java.util.ArrayList;
import java.util.List;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;

/**
 *
 * @author Sebastian
 */
public class ReportStatistic extends GraphQLObjectType {
    
    private static ReportStatistic instance;
    
    private static final GraphQLFieldDefinition REPORT = Query.getFieldDefinition("report", Report.getInstance());
    private static final GraphQLFieldDefinition STATISTIC = Query.getFieldDefinition("statistic", Statistic.getInstance());
    
    public ReportStatistic(String name, String description, List<GraphQLFieldDefinition> fieldDefinitions, List<GraphQLOutputType> interfaces) {
        super(name, description, fieldDefinitions, interfaces);
    }
    
    public static ReportStatistic getInstance() {
        if(instance != null)
            return instance;
        
        ArrayList<GraphQLFieldDefinition> defs = new ArrayList<>();
        defs.add(REPORT);
        defs.add(STATISTIC);        
        
        instance = new ReportStatistic("report_statistic", "", defs, new ArrayList<GraphQLOutputType>());
        return instance;
    }
    
}
