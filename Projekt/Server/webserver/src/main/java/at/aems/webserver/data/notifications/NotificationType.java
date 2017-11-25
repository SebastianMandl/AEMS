/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.data.notifications;

/**
 *
 * @author Niklas
 */
public enum NotificationType {

    INFORMATION(0), WARNING(1);
    
    private int typeId;
    
    private NotificationType(int typeId) {
        this.typeId = typeId;
    }
    
    public int getNumericId() {
        return typeId;
    }
    
    public static NotificationType byId(int id) {
        for(NotificationType type : NotificationType.values()) {
            if(type.getNumericId() == id) {
                return type;
            }
        }
        return null;
    }
}
