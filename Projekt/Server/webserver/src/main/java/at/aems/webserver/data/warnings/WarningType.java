/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.warnings;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Niggi
 */
public enum WarningType {
    
    @SerializedName("0")
    BENACHRICHTIGUNG, 
    
    @SerializedName("1")
    WARNUNG;
}
