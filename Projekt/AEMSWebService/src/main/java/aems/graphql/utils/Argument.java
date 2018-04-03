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
public class Argument {
    
    public static final int LTE = 0;
    public static final int GTE = 1;
    public static final int LIKE = 2;
    public static final int EQUAL = 3;
    
    public String name;
    public String column;
    
    public int comparison;
    
    public Argument(String name, String column, int comparison) {
        this.name = name;
        this.column = column;
        
        this.comparison = comparison;
    }
    
}
