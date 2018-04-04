/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javax.faces.bean.ManagedBean;

/**
 *
 * @author Sebastian
 */
@ManagedBean
public class Meter {
    
    private String id;
    private boolean isSensor;

    public boolean isIsSensor() {
        return isSensor;
    }

    public void setIsSensor(boolean isSensor) {
        this.isSensor = isSensor;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id + ";" + isSensor;
    }
    
    public static Meter fromString(String s) {
        String[] result = s.split(";");
        Meter meter = new Meter();
        meter.id = result[0];
        meter.isSensor = Boolean.valueOf(result[1]);
        return meter;
    }
    
}
