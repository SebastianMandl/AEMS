/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.display;

import at.aems.webserver.data.statistic.StatisticMeta;
import at.aems.webserver.data.warnings.WarningMeta;
import at.aems.webserver.data.warnings.WarningType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class UserWarningsBean {
    private List<WarningMeta> allWarnings = new ArrayList<>();

    public UserWarningsBean() {
    }
    
    @PostConstruct
    public void init() {
        WarningMeta m = new WarningMeta(1234, "Warning 1");
        m.setType(WarningType.WARNUNG);
        
        WarningMeta m2 = new WarningMeta(1235, "Notifi 1");
        m2.setType(WarningType.BENACHRICHTIGUNG);
        
        allWarnings.add(m);
        allWarnings.add(m2);
    }

    public List<WarningMeta> getAllNotifications() {
        return allWarnings;
    }

    public void setAllNotifications(List<WarningMeta> allWarnings) {
        this.allWarnings = allWarnings;
    }
    
    public List<WarningMeta> getWarnings() {
        final List<WarningMeta> result = new ArrayList<>();
        allWarnings.forEach(new Consumer<WarningMeta>() {
            @Override
            public void accept(WarningMeta t) {
                if (t.getType() == WarningType.WARNUNG) {
                    result.add(t);
                }
            }
        });
        return result;
    }
    
    public List<WarningMeta> getNotifications() {
                final List<WarningMeta> result = new ArrayList<>();
        allWarnings.forEach(new Consumer<WarningMeta>() {
            @Override
            public void accept(WarningMeta t) {
                if (t.getType() == WarningType.BENACHRICHTIGUNG) {
                    result.add(t);
                }
            }
        });
        return result;
    }
    
    
}
