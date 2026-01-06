package com.example.diabetes;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.util.List;

public class SimpleChartGenerator {

    public static LineChart<Number, Number> generateRiskTrendChart(List<PredictionHistory> history) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Prediction Number");
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Math.max(history.size(), 5));
        xAxis.setTickUnit(Math.max(1, history.size() / 5));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Risk Percentage (%)");
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(10);

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Diabetes Risk Trend Over Time");
        chart.setStyle("-fx-font-size: 14; -fx-padding: 15;");
        chart.setLegendVisible(true);
        chart.setPrefHeight(500);
        chart.setPrefWidth(800);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Risk %");

        if (!history.isEmpty()) {
            for (int i = 0; i < history.size(); i++) {
                PredictionHistory pred = history.get(history.size() - 1 - i);
                series.getData().add(new XYChart.Data<>(i + 1, pred.getRiskPercentage()));
            }
        }

        chart.getData().add(series);
        styleChart(chart);
        return chart;
    }

    public static LineChart<Number, Number> generateGlucoseTrendChart(List<PredictionHistory> history) {
        // Find min and max glucose values
        double minGlucose = history.stream()
            .mapToDouble(PredictionHistory::getGlucose)
            .min()
            .orElse(70);
        double maxGlucose = history.stream()
            .mapToDouble(PredictionHistory::getGlucose)
            .max()
            .orElse(150);

        // Add padding to make chart more visually clear
        double padding = (maxGlucose - minGlucose) * 0.15;
        double lowerBound = Math.max(40, minGlucose - padding);
        double upperBound = Math.min(300, maxGlucose + padding);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Prediction Number");
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Math.max(history.size(), 5));
        xAxis.setTickUnit(Math.max(1, history.size() / 5));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Glucose (mg/dL)");
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit((upperBound - lowerBound) / 10);

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Glucose Level Trend");
        chart.setStyle("-fx-font-size: 14; -fx-padding: 15;");
        chart.setLegendVisible(true);
        chart.setPrefHeight(500);
        chart.setPrefWidth(800);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Glucose");

        if (!history.isEmpty()) {
            for (int i = 0; i < history.size(); i++) {
                PredictionHistory pred = history.get(history.size() - 1 - i);
                series.getData().add(new XYChart.Data<>(i + 1, pred.getGlucose()));
            }
        }

        chart.getData().add(series);
        styleChart(chart);
        return chart;
    }

    public static LineChart<Number, Number> generateBMITrendChart(List<PredictionHistory> history) {
        // Find min and max BMI values
        double minBmi = history.stream()
            .mapToDouble(PredictionHistory::getBmi)
            .min()
            .orElse(18);
        double maxBmi = history.stream()
            .mapToDouble(PredictionHistory::getBmi)
            .max()
            .orElse(35);

        // Add padding to make chart more visually clear
        double padding = (maxBmi - minBmi) * 0.15;
        if (padding < 2) padding = 2;
        double lowerBound = Math.max(10, minBmi - padding);
        double upperBound = Math.min(50, maxBmi + padding);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Prediction Number");
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Math.max(history.size(), 5));
        xAxis.setTickUnit(Math.max(1, history.size() / 5));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("BMI (kg/m²)");
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit((upperBound - lowerBound) / 10);

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Body Mass Index (BMI) Trend");
        chart.setStyle("-fx-font-size: 14; -fx-padding: 15;");
        chart.setLegendVisible(true);
        chart.setPrefHeight(500);
        chart.setPrefWidth(800);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("BMI");

        if (!history.isEmpty()) {
            for (int i = 0; i < history.size(); i++) {
                PredictionHistory pred = history.get(history.size() - 1 - i);
                series.getData().add(new XYChart.Data<>(i + 1, pred.getBmi()));
            }
        }

        chart.getData().add(series);
        styleChart(chart);
        return chart;
    }

    public static LineChart<Number, Number> generateBloodPressureTrendChart(List<PredictionHistory> history) {
        // Find min and max BP values
        double minBp = history.stream()
            .mapToDouble(PredictionHistory::getBloodPressure)
            .min()
            .orElse(60);
        double maxBp = history.stream()
            .mapToDouble(PredictionHistory::getBloodPressure)
            .max()
            .orElse(140);

        // Add padding to make chart more visually clear
        double padding = (maxBp - minBp) * 0.15;
        if (padding < 5) padding = 5;
        double lowerBound = Math.max(40, minBp - padding);
        double upperBound = Math.min(200, maxBp + padding);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Prediction Number");
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Math.max(history.size(), 5));
        xAxis.setTickUnit(Math.max(1, history.size() / 5));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Blood Pressure (mmHg)");
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit((upperBound - lowerBound) / 10);

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Blood Pressure Trend");
        chart.setStyle("-fx-font-size: 14; -fx-padding: 15;");
        chart.setLegendVisible(true);
        chart.setPrefHeight(500);
        chart.setPrefWidth(800);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("BP");

        if (!history.isEmpty()) {
            for (int i = 0; i < history.size(); i++) {
                PredictionHistory pred = history.get(history.size() - 1 - i);
                series.getData().add(new XYChart.Data<>(i + 1, pred.getBloodPressure()));
            }
        }

        chart.getData().add(series);
        styleChart(chart);
        return chart;
    }

    public static LineChart<Number, Number> generateInsulinTrendChart(List<PredictionHistory> history) {
        // Find min and max insulin values
        double minInsulin = history.stream()
            .mapToDouble(PredictionHistory::getInsulin)
            .min()
            .orElse(0);
        double maxInsulin = history.stream()
            .mapToDouble(PredictionHistory::getInsulin)
            .max()
            .orElse(100);

        // Add padding to make chart more visually clear
        double padding = (maxInsulin - minInsulin) * 0.15;
        if (padding < 5) padding = 5;
        double lowerBound = Math.max(0, minInsulin - padding);
        double upperBound = maxInsulin + padding;

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Prediction Number");
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(Math.max(history.size(), 5));
        xAxis.setTickUnit(Math.max(1, history.size() / 5));

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Insulin (μU/ml)");
        yAxis.setLowerBound(lowerBound);
        yAxis.setUpperBound(upperBound);
        yAxis.setTickUnit((upperBound - lowerBound) / 10);

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Insulin Level Trend");
        chart.setStyle("-fx-font-size: 14; -fx-padding: 15;");
        chart.setLegendVisible(true);
        chart.setPrefHeight(500);
        chart.setPrefWidth(800);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Insulin");

        if (!history.isEmpty()) {
            for (int i = 0; i < history.size(); i++) {
                PredictionHistory pred = history.get(history.size() - 1 - i);
                series.getData().add(new XYChart.Data<>(i + 1, pred.getInsulin()));
            }
        }

        chart.getData().add(series);
        styleChart(chart);
        return chart;
    }

    private static void styleChart(LineChart<Number, Number> chart) {
        // Apply CSS styling for better appearance
        chart.setStyle(chart.getStyle() + "; -fx-legend-visible: true;");
        
        // Make the chart background match the app theme
        chart.setStyle("-fx-font-size: 13px; -fx-padding: 15px; " +
                      "-fx-background-color: #333333; " +
                      "-fx-border-color: #444444; -fx-border-width: 1;");
    }
}
