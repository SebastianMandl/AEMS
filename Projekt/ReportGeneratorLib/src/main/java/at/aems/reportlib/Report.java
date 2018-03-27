package at.aems.reportlib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.aems.apilib.AemsUser;

public class Report {
    
    private Integer reportId;
    private String reportName;
    private String reportAnnotation;
    private List<Integer> statistics;
    
    public Report(Integer reportId) {
        this.reportId = reportId;
        this.statistics = new ArrayList<Integer>();
    }
    
    public Report() {
        this(null);
    }
    
    public void setStatistics(List<Integer> statistics) {
        this.statistics = statistics;
    }
    
    /**
     * Returns an unmodifiable list of all statistic IDs
     */
    public List<Integer> getStatistics() {
        return Collections.unmodifiableList(statistics);
    }
    
    public void addStatistic(Integer id) {
        this.statistics.add(id);
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

    public void fetch(AemsUser user) {
        
        boolean fetchMetadata = reportName == null || reportAnnotation == null;
        boolean fetchStatistics = statistics.isEmpty();
        
    }
    
}
