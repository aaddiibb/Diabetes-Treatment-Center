package com.example.diabetes;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class ChartGenerator {

    public static JFreeChart generateRiskTrendChart(List<PredictionHistory> history) {
        TimeSeries series = new TimeSeries("Diabetes Risk (%)");

        for (PredictionHistory pred : history) {
            Date date = java.sql.Timestamp.valueOf(pred.getPredictionDate()).toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay()
                    .atZone(ZoneId.systemDefault()).toInstant()
                    .toEpochMilli() > 0 ? new Date(java.sql.Timestamp.valueOf(pred.getPredictionDate()).getTime()) : new Date();
            Day day = new Day(date);
            series.addOrUpdate(day, pred.getRiskPercentage());
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Diabetes Risk Trend Over Time",
                "Date",
                "Risk Percentage (%)",
                dataset,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setDateFormatOverride(new SimpleDateFormat("dd-MMM-yyyy"));

        return chart;
    }

    public static JFreeChart generateGlucoseTrendChart(List<PredictionHistory> history) {
        TimeSeries series = new TimeSeries("Glucose (mg/dL)");

        for (PredictionHistory pred : history) {
            Date date = new Date(java.sql.Timestamp.valueOf(pred.getPredictionDate()).getTime());
            Day day = new Day(date);
            series.addOrUpdate(day, pred.getGlucose());
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Glucose Level Trend",
                "Date",
                "Glucose (mg/dL)",
                dataset,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setDateFormatOverride(new SimpleDateFormat("dd-MMM-yyyy"));

        return chart;
    }

    public static JFreeChart generateBMITrendChart(List<PredictionHistory> history) {
        TimeSeries series = new TimeSeries("BMI (kg/m²)");

        for (PredictionHistory pred : history) {
            Date date = new Date(java.sql.Timestamp.valueOf(pred.getPredictionDate()).getTime());
            Day day = new Day(date);
            series.addOrUpdate(day, pred.getBmi());
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Body Mass Index (BMI) Trend",
                "Date",
                "BMI (kg/m²)",
                dataset,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setDateFormatOverride(new SimpleDateFormat("dd-MMM-yyyy"));

        return chart;
    }

    public static JFreeChart generateBloodPressureTrendChart(List<PredictionHistory> history) {
        TimeSeries series = new TimeSeries("Blood Pressure (mmHg)");

        for (PredictionHistory pred : history) {
            Date date = new Date(java.sql.Timestamp.valueOf(pred.getPredictionDate()).getTime());
            Day day = new Day(date);
            series.addOrUpdate(day, pred.getBloodPressure());
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Blood Pressure Trend",
                "Date",
                "Blood Pressure (mmHg)",
                dataset,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setDateFormatOverride(new SimpleDateFormat("dd-MMM-yyyy"));

        return chart;
    }

    public static JFreeChart generateInsulinTrendChart(List<PredictionHistory> history) {
        TimeSeries series = new TimeSeries("Insulin (μU/ml)");

        for (PredictionHistory pred : history) {
            Date date = new Date(java.sql.Timestamp.valueOf(pred.getPredictionDate()).getTime());
            Day day = new Day(date);
            series.addOrUpdate(day, pred.getInsulin());
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Insulin Level Trend",
                "Date",
                "Insulin (μU/ml)",
                dataset,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        DateAxis domainAxis = (DateAxis) plot.getDomainAxis();
        domainAxis.setDateFormatOverride(new SimpleDateFormat("dd-MMM-yyyy"));

        return chart;
    }
}
