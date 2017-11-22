/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data;

/**
 * Represents a statistic of the aems system
 * @author Niklas
 */
public class Statistic {
    private String id;
    private String name;
    /* Whether the statistic should be shown at the start page */
    private boolean showHome;
    
    /* Whether the statistic should be shown in the android app */
    private boolean showAndroid;
}
