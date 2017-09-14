/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aems;

/**
 *
 * @author Sebastian
 */
public abstract class Condition {
    
    private final String PATTERN;
    
    public Condition(String pattern) {
        this.PATTERN = pattern;
    }
    
    public boolean matches(String input) {
        return input.matches(PATTERN);
    }
    
    public abstract String exec();
    
}
