package at.aems.reportlib;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import at.aems.apilib.AemsAPI;
import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUser;

public class Main {

    public static final String CONFIG_FILENAME = "config.json";

    public static void main(String[] args) throws Exception {
        Config.createIfMissing(CONFIG_FILENAME);
        Config conf = Config.read(CONFIG_FILENAME);

        AemsUser rootUser = new AemsUser(conf.getUserId(), conf.getUsername(), conf.getPassword());
        List<Report> reports = queryAllReports(rootUser, conf);
        
        String savePath = conf.getPdfFolder();
        for(Report r : reports) {
            r.fetch(rootUser);
            if(!r.shouldGenerate())
                continue;
            
            PDDocument pdf = r.toPdf(rootUser);

            File folder = new File(savePath, "" + r.getUserId());
            if(!folder.exists()) {
                folder.mkdirs();
            }
            
            File file = new File(folder, r.getReportName() + "_" + currentDate());
            pdf.save(file + ".pdf");
        } 
    }

    private static String currentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        return sdf.format(new Date());
    }

    private static List<Report> queryAllReports(AemsUser rootUser, Config c) {
        
        AemsQueryAction query = new AemsQueryAction(rootUser);
        query.setQuery("{ reports { id } }");
        
        AemsAPI.setUrl(c.getApiUrl());
        AemsResponse response = null;
        try {
            response = AemsAPI.call0(query, null);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        
        if(response == null || !response.isOk()) {
            return null;
        }
        
        List<Report> reports = new ArrayList<>();
        for(JsonElement e : response.getJsonArrayWithinObject()) {
            JsonObject obj = e.getAsJsonObject();
            if(obj.has("id")) {
                int id = obj.get("id").getAsInt();
                reports.add(new Report(id));
            }
        }
        return reports;
        
    }

}
