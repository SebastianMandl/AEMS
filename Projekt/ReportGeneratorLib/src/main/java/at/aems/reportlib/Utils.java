package at.aems.reportlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import at.aems.apilib.AbstractAemsAction;
import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsResponse;

public class Utils {
    
    public static final String AEMS_API_URL = "http://aemsserver.ddns.net:8084/AEMSWebService/RestInf";
    
    public static String readContents(String resource) {
        InputStream stream = Utils.class.getClassLoader().getResourceAsStream(resource);
        String raw;
        try {
            raw = readDataFromStream(stream);
        } catch(IOException ex) {
            raw = null;
        }
        return raw;
    }
    
    public static String addPlaceholders(String rawString, Object...values) {
        String questionMark = Pattern.quote("?");
        for(Object str : values) {
            rawString = rawString.replaceFirst(questionMark, String.valueOf(str));
        }
        return rawString;
    }
    
    public static AemsResponse callApi(AbstractAemsAction action) {
        AemsAPI.setUrl(AEMS_API_URL);
        try {
            AemsResponse response = AemsAPI.call0(action, null);
            return response;
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static <T> List<T> toPrimitiveArray(JsonArray array, Class<?> cls) {
        List<T> result = new ArrayList<>();
        
        for(JsonElement e : array) {
            if(!e.isJsonPrimitive()) {
                throw new IllegalArgumentException("json array may only contain primitives");
            }
            
        }
        
        return result;
    }
    
    public static List<String> getLabels(int periodId) {
        switch(periodId) {
        case 1: 
            return Arrays.asList("0-3", "3-6", "6-9", "9-12", "12-15", "15-18", "18-21", "21-24");
        case 2:
            return Arrays.asList("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag");
        case 3: 
            return Arrays.asList("KW x", "KW x+1", "KW x+2", "KW x+3", "KW x+4");
        case 4:
            return Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli",
                    "August", "September", "Oktober", "November", "Dezember");
        }
        return null;
    }
    
    public static int getAmountOfValues(int periodId) {
        return getLabels(periodId).size();
    }
    
    private static String readDataFromStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
            buffer.append(System.lineSeparator());
        }
        return buffer.toString();
    }
}
