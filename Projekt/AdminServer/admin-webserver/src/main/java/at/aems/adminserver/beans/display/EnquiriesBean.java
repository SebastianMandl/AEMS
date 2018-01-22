/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.adminserver.beans.display;

import at.aems.adminserver.beans.UserBean;
import at.aems.adminserver.data.users.Enquiry;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
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
public class EnquiriesBean extends AbstractDisplayBean {
    
    private List<Enquiry> enquiries = new ArrayList<>();
    
    @ManagedProperty(value="#{userBean}")
    private UserBean userBean;

    public EnquiriesBean() {
    }

    public List<Enquiry> getEnquiries() {
        return enquiries;
    }

    public void setEnquiries(List<Enquiry> enquiries) {
        this.enquiries = enquiries;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    @Override
    public void update() {
        enquiries.clear();
        System.out.println(" ------ Update called on " + this.getClass().getSimpleName());
        Enquiry e = new Enquiry("graf@graf.graf", "Graf", true, new Timestamp(System.currentTimeMillis() - 1500000));
        Enquiry e2 = new Enquiry("knoll@knolli.k", "Knolli", true, new Timestamp(System.currentTimeMillis() - 1500000000));
        Enquiry e3 = new Enquiry("mandl@mandl.m", "Mandl", true, new Timestamp(System.currentTimeMillis() - 150000000000L));

        enquiries.add(e);
        enquiries.add(e2);
        enquiries.add(e3);
        
        Collections.sort(enquiries);
    }
    
    
    
    
}
