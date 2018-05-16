/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.aems.webserver;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.servlet.ServletContext;

/**
 * This class provides utility methods to access data from the aems api.
 *
 * @author Niklas
 */
public class AemsUtils {
 
    public static final String API_URL = ServerConfig.read().getApiUrl();
	    //"aemsserver.ddns.net:8084/AEMSWebService/RestInf";
   
    public static final ServerConfig CONFIG = ServerConfig.read();

    public static boolean isInt(String s) {
	try {
	    Integer.parseInt(s);
	    return true;
	} catch(NumberFormatException ex) {
	    return false;
	}
    }

    public static JsonStructure call(String t, Map<String, Object> params) {
        Map<String, String> m = new HashMap<>(1);
        try {
            URL url = new URL(API_URL + t);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            byte[] postData = encodePostData(params);

            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("charset", "utf-8");
            con.setRequestProperty("Content-Length", Integer.toString(postData.length));
            con.setUseCaches(false);
            // Write POST data to output stream
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postData);
                wr.flush();
                wr.close();
            }
            con.connect();
            String resultString = readResult(con);

            JsonReader jsonReader = Json.createReader(new StringReader(resultString));
            JsonStructure result;
            if (resultString.startsWith("[")) {
                result = jsonReader.readArray();
            } else {
                result = jsonReader.readObject();
            }
            jsonReader.close();

            return result;

        } catch (MalformedURLException ex) {
            Logger.getLogger(AemsUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AemsUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String getQuery(String name, Map<String, String> placeholders) {
        ServletContext servlet = (ServletContext) FacesContext.getCurrentInstance()
                .getExternalContext().getContext();
        String basePath = servlet.getRealPath("/") + "query";

        File queryFolder = new File(basePath);
        List<File> allFiles = listAllFiles(queryFolder);

        try {
            for (File file : allFiles) {
                if (file.getName().startsWith(name)) {
                    String fileContents = String.join(System.lineSeparator(), Files.readAllLines(file.toPath()));
                    if (placeholders != null) {
                        for (Entry<String, String> entry : placeholders.entrySet()) {
                            fileContents = fileContents.replaceAll("#" + entry.getKey() + "#", entry.getValue());
                        }
                    }
                    return fileContents;
                }
            }
        } catch (IOException e) {
            Logger.getLogger(AemsUtils.class.getName()).log(Level.SEVERE, null, e);
        }

        throw new IllegalArgumentException("No query file found for name: " + name);

    }

    private static List<File> listAllFiles(File folder) {
        List<File> files = new ArrayList<>();
        for(File f : folder.listFiles()) {
            if(f.isFile())
                files.add(f);
            else
                files.addAll(listAllFiles(f));
        }
        return files;
    }

    private static byte[] encodePostData(Map<String, Object> params) {
        StringBuilder paramBuilder = new StringBuilder();
        boolean first = true;
        for (Entry<String, Object> e : params.entrySet()) {
            if (!first) {
                paramBuilder.append("&");
            }
            paramBuilder.append(e.getKey()).append("=").append(e.getValue());
            first = false;
        }
        byte[] postData = paramBuilder.toString().getBytes(StandardCharsets.UTF_8);
        return postData;
    }

    private static String readResult(HttpURLConnection con) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (IOException e) {
            // ignore
        }
        return null;
    }

    public static List<String> asStringList(String s) {
        String[] arr = s.split(";");
        List<String> res = new ArrayList<>(Arrays.asList(arr));
        if (res.isEmpty()) {
            return res;
        }
        String last = res.get(res.size() - 1);
        if (last == null || last.length() == 0) {
            res.remove(last);
        }
        return res;
    }

    public static List<Integer> asIntList(String s) {
        List<String> strings = asStringList(s);
        List<Integer> ints = new ArrayList<>();
        for (String str : strings) {
            ints.add(Integer.parseInt(str));
        }
        return ints;
    }

    public static String decodeBase64(String encoded) {
        return new String(Base64.getUrlDecoder().decode(encoded.getBytes()));
    }

    public static int getResponseId(String json) {
        JsonObject ob = new JsonParser().parse(json).getAsJsonObject();
        if (ob.has("id")) {
            return ob.get("id").getAsInt();
        }
        return -1;
    }

}
