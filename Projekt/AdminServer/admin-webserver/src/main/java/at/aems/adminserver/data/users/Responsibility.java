/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.data.users;

/**
 *
 * @author Niggi
 */
public class Responsibility {
    private String postalCode;
    private String name;

    public Responsibility(String postalCode, String name) {
        this.postalCode = postalCode;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return postalCode + " " + name;
    }
    
}
