package at.aems.reportlib;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class AemsChart {

    private String title;
    private List<String> labels;
    private List<Double> values;
    private List<Double> prevValues;
    private List<Double> anomalyValues;
    private String anomalyName;

    private String leftLabel;
    private String rightLabel;

    public AemsChart(String title) {
        this.title = title;
    }

    public JFreeChart toChart() {
        CategoryPlot plot = new CategoryPlot();
        
        CategoryItemRenderer lineRenderer = new LineAndShapeRenderer();
        lineRenderer.setSeriesStroke(1, new BasicStroke(10));
        if(anomalyValues != null)
            plot.setDataset(1, asDataset(anomalyName, anomalyValues));
        plot.setRenderer(1, lineRenderer);

        CategoryItemRenderer barRanderer = new BarRenderer();
        ((BarRenderer) barRanderer).setItemMargin(0.1);

        Color c1 = new Color(247, 238, 12);
        Color c2 = new Color(255, 249, 130);

        barRanderer.setSeriesPaint(0, c1);
        barRanderer.setSeriesPaint(1, c2);
        plot.setDataset(0, asDataset(values, prevValues));
        plot.setRenderer(0, barRanderer);

        plot.setDomainAxis(new CategoryAxis("Legende"));
        plot.setRangeAxis(0, new NumberAxis(leftLabel));
        if(anomalyValues != null)
            plot.setRangeAxis(1, new NumberAxis(rightLabel));

        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        plot.mapDatasetToRangeAxis(1, 1);

        JFreeChart chart = new JFreeChart(plot);
        chart.setTitle(title);
        return chart;
    }

    private DefaultCategoryDataset asDataset(String name, List<Double> list) {
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        if(list == null)
            return data;
        
        for(int i = 0; i < list.size(); i++) {
            data.addValue(list.get(i), name, labels.get(i));
        }
        
        return data;
    }
    
    public BufferedImage toBufferedImage() {
        return toChart().createBufferedImage(600, 440); 
    }
    
    private DefaultCategoryDataset asDataset(List<Double> list, List<Double> listPre) {
        DefaultCategoryDataset data = new DefaultCategoryDataset();
        if(list == null)
            return data;
        
        for(int i = 0; i < list.size(); i++) {
            data.addValue(list.get(i), "Aktuell", labels.get(i));
            if(listPre != null && listPre.size() == list.size()) {
                data.addValue(listPre.get(i), "Vorperiode", labels.get(i));
            }
            
        }
        
        return data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Double> getValues() {
        return values;
    }

    public void setValues(List<Double> values) {
        this.values = values;
    }

    public List<Double> getPrevValues() {
        return prevValues;
    }

    public void setPrevValues(List<Double> prevValues) {
        this.prevValues = prevValues;
    }

    public List<Double> getAnomalyValues() {
        return anomalyValues;
    }

    public void setAnomalyValues(List<Double> anomalyValues) {
        this.anomalyValues = anomalyValues;
    }
    
    public String getAnomalyName() {
        return anomalyName;
    }
    
    public void setAnomalyName(String anomalyName) {
        this.anomalyName = anomalyName;
    }

    public String getLeftLabel() {
        return leftLabel;
    }

    public void setLeftLabel(String leftLabel) {
        this.leftLabel = leftLabel;
    }

    public String getRightLabel() {
        return rightLabel;
    }

    public void setRightLabel(String rightLabel) {
        this.rightLabel = rightLabel;
    }

}
