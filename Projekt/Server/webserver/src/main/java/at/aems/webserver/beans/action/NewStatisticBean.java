/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.action;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsInsertAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUser;
import at.aems.apilib.crypto.EncryptionType;
import at.aems.webserver.AemsUtils;
import at.aems.webserver.beans.UserBean;
import at.aems.webserver.data.statistic.Period;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.SelectItem;

/**
 *
 * @author Niklas
 */
@ManagedBean(name = "newStatistic")
public class NewStatisticBean extends AbstractActionBean {

    private Period periods;
    
    private String name;
    private List<String> meters = new ArrayList<>();
    private int period;
    private boolean compare;
    
    private String annotation;

    public NewStatisticBean() {
    }

    public SelectItem[] getPeriodValues() {
        SelectItem[] items = new SelectItem[Period.values().length];
        int i = 0;
        for (Period p : Period.values()) {
            items[i++] = new SelectItem(p, p.getLabel());
        }
        return items;
    }
    
    public String getMeters() {
        return String.join(";", meters);
    }
    
    public void setMeters(String meters) {
        this.meters = AemsUtils.asStringList(meters);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
       this.period = period;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
       this.annotation = annotation;
    }

    public boolean isCompare() {
        return compare;
    }

    public void setCompare(boolean compare) {
        this.compare = compare;
    }

    public String doCreate() throws Exception {
        AemsUser user = new AemsUser(userBean.getUserId(), userBean.getUsername(), userBean.getPassword());
        AemsInsertAction insert = new AemsInsertAction(user, EncryptionType.SSL);
        insert.setTable("Statistics");
        insert.beginWrite();
        insert.write("user", user.getUserId());
        insert.write("name", name);
//        insert.write("period", period);
        insert.write("annotation", annotation);
//        insert.write("period_compare", compare);
        insert.endWrite();
        
	configureApiParams();
        AemsResponse resp = AemsAPI.call0(insert, null); 
	System.out.println(resp.getDecryptedResponse());
        int statisticId = resp.getAsJsonObject().get("id").getAsInt();
        
        AemsInsertAction insertMeters = new AemsInsertAction(user, EncryptionType.SSL);
        insertMeters.setTable("SatatisticMeters");
        insertMeters.beginWrite();
        for(String m : meters) {
            insertMeters.write("statistic", statisticId);
            insertMeters.write("meter", m);
            insertMeters.endWrite();
        }
         
        AemsResponse resp2 = AemsAPI.call0(insertMeters, null);
        notify.setMessage("Statistik wurde erstellt!");
        callUpdateOn("statisticBean");
        return "einstellungenStatistiken";
    }

}
