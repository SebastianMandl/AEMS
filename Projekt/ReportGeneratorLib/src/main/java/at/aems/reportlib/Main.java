package at.aems.reportlib;

import java.util.Arrays;

import org.apache.pdfbox.pdmodel.PDDocument;

import at.aems.apilib.AemsUser;

public class Main {

    public static void main(String[] args) throws Exception {
        
        AemsUser user = new AemsUser(185, "x", "pwd");
        Report r = new Report(104);
        r.fetch(user);
        
        PDDocument doc = r.toPdf(user);
        doc.save(r.getReportName().replaceAll(" ", "_") + ".pdf");
        
    }

}
