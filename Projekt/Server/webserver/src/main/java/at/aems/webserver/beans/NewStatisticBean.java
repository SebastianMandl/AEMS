/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niklas
 */
@ManagedBean(name="statistic")
public class NewStatisticBean implements Serializable {
    private String name;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
}
