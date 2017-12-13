/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data;

/**
 *
 * @author Niklas
 */
public enum ApiAction {
    
    QUERY,
    CREATE_STATISTIC, ALTER_STATISTIC, DELETE_STATISTIC,
    CREATE_REPORT, ALTER_REPORT, DELETE_REPORT,
    CREATE_SAVEPOINT, ALTER_SAVEPOINT, DELETE_SAVEPOINT,
    CREATE_NOTIFICATION, ALTER_NOTIFICATION, DELETE_NOTIFICATION;
    
}
