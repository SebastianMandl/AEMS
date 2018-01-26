/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.display;

import at.aems.adminserver.beans.UserBean;
import at.aems.adminserver.data.users.DisplayedUser;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public class AdminDisplayBean extends AbstractDisplayBean {
    
    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;
    
    private List<DisplayedUser> admins;
    private List<DisplayedUser> subAdmins;

    @Override
    public void update() {
        admins = new ArrayList<>();
        subAdmins =  new ArrayList<>();
        DisplayedUser u1 = new DisplayedUser(123, "admin", "admin.admin@admin.a");
        DisplayedUser u2 = new DisplayedUser(101, "Not Admin", "notadmin.admin@admin.a");
        
        admins.add(u1);
        subAdmins.add(u2);
        
        List<DisplayedUser> newAdminList = new ArrayList<>();
        for(DisplayedUser u : admins) {
            if(u.getId() != userBean.getUserId()) {
                newAdminList.add(u);
            }
        }

        admins = newAdminList;
        
    }

    public List<DisplayedUser> getAdmins() {
        return admins;
    }

    public void setAdmins(List<DisplayedUser> admins) {
        this.admins = admins;
    }

    public List<DisplayedUser> getSubAdmins() {
        return subAdmins;
    }

    public void setSubAdmins(List<DisplayedUser> subAdmins) {
        this.subAdmins = subAdmins;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }
    
    
    
    
    
}
