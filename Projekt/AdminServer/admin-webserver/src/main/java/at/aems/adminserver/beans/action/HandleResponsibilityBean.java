/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.action;

import at.aems.adminserver.beans.display.ResponsibilityBean;
import at.aems.adminserver.data.users.Responsibility;
import java.util.List;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class HandleResponsibilityBean extends AbstractActionBean {
    private String postalCode;
    private String placeName;

    public HandleResponsibilityBean() {
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String doAdd() {
        System.out.println(postalCode + " " + placeName);
        // Check if user already has responsibility with that postal code
        List<Responsibility> resp = ((ResponsibilityBean)getDisplayBean("responsibilityBean")).getResponsibilities();
        for(Responsibility r : resp) {
            if(r.getPostalCode().equals(postalCode)) {
                notify.setMessage("Dieser Zuständigkeitsbereich existiert bereits");
                return "zustaendigkeit";
            }
        }
  
        notify.setMessage("Zuständigkeitsbereich wurde hinzugefügt!");
        return "zustaendigkeit";
    }
    
    public String doDelete() {
        notify.setMessage("Zuständigkeitsbereich " + postalCode + " wurde gelöscht!");
        return "zustaendigkeit";
    }
    
    public String doAlter() {
        notify.setMessage("Zuständigkeitsbereich wurde bearbeitet!");
        return "zustaendigkeit";
    }
    
}
