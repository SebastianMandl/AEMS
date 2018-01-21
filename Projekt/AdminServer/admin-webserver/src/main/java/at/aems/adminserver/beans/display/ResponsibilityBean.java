/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.display;

import at.aems.adminserver.beans.action.AbstractActionBean;
import at.aems.adminserver.data.users.Responsibility;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public class ResponsibilityBean extends AbstractDisplayBean {
    private List<Responsibility> responsibilities = new ArrayList<>();

    public ResponsibilityBean() {
    }
    
    public List<Responsibility> getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(List<Responsibility> responsibilities) {
        this.responsibilities = responsibilities;
    }

    @Override
    public void update() {
        System.out.println(" ------ Update called on " + this.getClass().getSimpleName());
        Responsibility r = new Responsibility("1234", "Musterort");
        responsibilities.add(r);
    }
    
    
}
