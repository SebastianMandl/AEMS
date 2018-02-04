/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver.beans.action;

import at.aems.webserver.beans.AbstractBean;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Niggi
 */
@ManagedBean
public class WarningActionBean extends AbstractActionBean {
    public String doDelete(Integer id) {
        notify.setMessage("Benachrichtigung wurde entfernt!");
        return "einstellungenWarnungen";
    }
}
