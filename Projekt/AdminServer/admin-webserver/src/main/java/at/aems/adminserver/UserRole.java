/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver;

/**
 *
 * @author Niggi
 */
public enum UserRole {
    UNVERIFIED(0), UNREGISTERED(1), MEMBER(2), SUB_ADMIN(3), ADMIN(4);
    
    private final int id;
    
    UserRole(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public static UserRole getById(int id) {
        for(UserRole role : UserRole.values()) {
            if(role.getId() == id) {
                return role;
            }
        }
        return null;
    }
}
