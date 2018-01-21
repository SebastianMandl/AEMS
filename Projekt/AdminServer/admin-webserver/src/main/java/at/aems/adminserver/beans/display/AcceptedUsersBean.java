/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.display;

import at.aems.adminserver.beans.UserBean;
import at.aems.adminserver.data.users.AcceptedUser;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Niggi
 */
@ManagedBean
@SessionScoped
public class AcceptedUsersBean extends AbstractDisplayBean {

    private List<AcceptedUser> users = new ArrayList<>();

    @ManagedProperty(value = "#{userBean}")
    private UserBean userBean;

    public AcceptedUsersBean() {
    }


    public List<AcceptedUser> getUsers() {
        return users;
    }

    public void setUsers(List<AcceptedUser> users) {
        this.users = users;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    @Override
    public void update() {
        System.out.println(" ------ Update called on " + this.getClass().getSimpleName());
                AcceptedUser u = new AcceptedUser(123, "Josef", "doppelbauer@gmx.net");
        users.add(u);
    }

}
