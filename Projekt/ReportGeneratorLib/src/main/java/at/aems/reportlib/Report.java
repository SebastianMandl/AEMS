package at.aems.reportlib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import at.aems.apilib.AemsQueryAction;
import at.aems.apilib.AemsResponse;
import at.aems.apilib.AemsUser;

public class Report {

    private Integer reportId;
    private Integer userId;
    private Integer periodId;
    private String reportName;
    private String reportAnnotation;
    private List<Statistic> statistics;

    public Report(Integer reportId) {
        this.reportId = reportId;
        this.statistics = new ArrayList<>();
    }

    public Report() {
        this(null);
    }

    public void setStatistics(List<Statistic> statistics) {
        this.statistics = statistics;
    }

    /**
     * Returns an unmodifiable map of all statistic IDs
     */
    public List<Statistic> getStatistics() {
        return Collections.unmodifiableList(statistics);
    }

    public void addStatistic(Statistic data) {
        this.statistics.add(data);
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportAnnotation() {
        return reportAnnotation;
    }

    public void setReportAnnotation(String reportAnnotation) {
        this.reportAnnotation = reportAnnotation;
    }

    public Integer getUserId() {
        return userId;
    }

    public void fetch(AemsUser user) {

        if (reportId == null) {
            return; // we can't fetch anything if we don't know an id
        }

        boolean fetchMetadata = reportName == null || reportAnnotation == null || userId == null;
        boolean fetchStatistics = statistics.isEmpty();

        if (fetchMetadata) {
            String metaQuery = Utils.readContents("reportmeta_query");
            metaQuery = Utils.addPlaceholders(metaQuery, this.reportId);

            AemsQueryAction query = new AemsQueryAction(user);
            query.setQuery(metaQuery);

            AemsResponse response = Utils.callApi(query);
            if (!response.isOk()) {
                throw new RuntimeException(response.getExcetption());
            }

            JsonObject obj = response.getJsonArrayWithinObject().get(0).getAsJsonObject();
            JsonObject userObj = obj.get("user").getAsJsonObject();
            JsonObject periodObj = obj.get("period").getAsJsonObject();
            if (reportName == null && obj.has("name") && obj.get("name") != JsonNull.INSTANCE) {
                reportName = obj.get("name").getAsString();
            }
            if (reportAnnotation == null && obj.has("annotation") && obj.get("annotation") != JsonNull.INSTANCE) {
                reportAnnotation = obj.get("annotation").getAsString();
            }
            if (userId == null && userObj.has("id") && userObj.get("id") != JsonNull.INSTANCE) {
                userId = userObj.get("id").getAsInt();
            }

            this.periodId = periodObj.get("id").getAsInt();
        }

        if (fetchStatistics) {
            String statisticQuery = Utils.readContents("statistic_query");
            statisticQuery = Utils.addPlaceholders(statisticQuery, this.reportId);

            AemsQueryAction query = new AemsQueryAction(user);
            query.setQuery(statisticQuery);

            AemsResponse response = Utils.callApi(query);
            if (!response.isOk()) {
                throw new RuntimeException(response.getExcetption());
            }

            JsonArray statistics = response.getJsonArrayWithinObject();
            for (JsonElement element : statistics) {
                try {
                    int id = element.getAsJsonObject().get("statistic").getAsJsonObject().get("id").getAsInt();
                    Statistic data = new Statistic(id);
                    data.fetch(user);
                    this.statistics.add(data);
                } catch (Exception ex) {
                    // ignore
                }
            }
        }

    }

    public PDDocument toPdf(AemsUser user) throws IOException {
        PDDocument document = new PDDocument();
        int counter = 1;

        if (this.statistics.isEmpty()) {
            PDPage page = new PDPage();
            document.addPage(page);
        }

        for (Statistic statistic : this.statistics) {

            AemsChart chart = new AemsChart(statistic.getTitle());
            chart.setLabels(Utils.getLabels(statistic.getPeriodId()));
            chart.setValues(statistic.getConsumptionValues());
            chart.setPrevValues(statistic.getPreviousValues());
            chart.setLeftLabel("x");
            chart.setRightLabel("y");

            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream stream = new PDPageContentStream(document, page);
            stream.beginText();
            stream.setFont(PDType1Font.HELVETICA, 24);
            stream.newLineAtOffset(20, 760);
            stream.showText(reportName + " (Seite " + counter + ")");
            stream.endText();

            // chart image
            PDImageXObject img = JPEGFactory.createFromImage(document, chart.toBufferedImage());
            stream.drawImage(img, 5, 250, 600, 440);

            stream.close();
            counter++;
        }

        return document;
    }

    public boolean shouldGenerate() {
        GregorianCalendar calendar = new GregorianCalendar();
        switch (this.periodId) {
            case 1:
                return true;
            case 2:
                return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;
            case 3:
                return calendar.get(Calendar.DAY_OF_MONTH) == 1;
            case 4:
                return calendar.get(Calendar.DAY_OF_YEAR) == 1;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Report {" + this.reportId + "}";
    }

}
