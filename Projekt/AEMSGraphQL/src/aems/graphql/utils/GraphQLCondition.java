/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aems.graphql.utils;

/**
 *
 * @author Sebastian
 */
public abstract class GraphQLCondition {
    
    private final String PATTERN;
    
    public GraphQLCondition(String pattern) {
        this.PATTERN = pattern;
    }
    
    public boolean matches(String input) {
        return input.matches(PATTERN);
    }
    
    public abstract String exec();
    
}
