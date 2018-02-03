/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Niggi
 */
public class NewMap {
    public static Map<String, String> of(Object...values) {
        Map<String, String> map = new HashMap<>();
        for(int i = 0;i < values.length - 1; i++) {
            map.put(values[i].toString(), values[i+1].toString());
        }
        return map;
    }
}
