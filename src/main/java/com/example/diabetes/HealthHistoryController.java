package com.example.diabetes;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HealthHistoryController {

    @FXML private BorderPane mainPane;
    @FXML private TabPane tabPane;
    @FXML private TableView<PredictionHistory> historyTable;
    @FXML private TableColumn<PredictionHistory, String> dateColumn;
    @FXML private TableColumn<PredictionHistory, Double> riskColumn;
    @FXML private TableColumn<PredictionHistory, String> levelColumn;
    @FXML private TableColumn<PredictionHistory, Double> glucoseColumn;
    @FXML private TableColumn<PredictionHistory, Double> bmiColumn;
    @FXML private Button downloadReportBtn;
    @FXML private Button backBtn;
    @FXML private ComboBox<String> chartTypeCombo;
    @FXML private Label summaryLabel;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button filterButton;
    @FXML private Button clearFilterButton;

    // Statistics labels - need to be declared as FXML injected
    private Label glucoseMinLabel, glucoseMaxLabel, glucoseAvgLabel;
    private Label bmiMinLabel, bmiMaxLabel, bmiAvgLabel;
    private Label bpMinLabel, bpMaxLabel, bpAvgLabel;
    private Label riskMinLabel, riskMaxLabel, riskAvgLabel;
    
    // Chart container
    private VBox chartContainer;

    private final PredictionHistoryDAO historyDAO = new PredictionHistoryDAO();
    private User currentUser;
    private List<PredictionHistory> userHistory;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupChartTypes();
        loadUserHistory();
    }

    private void setupTableColumns() {
        dateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getPredictionDate().toString()
            )
        );
        
        riskColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(
                cellData.getValue().getRiskPercentage()
            )
        );
        
        levelColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getRiskLevel()
            )
        );
        
        glucoseColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(
                cellData.getValue().getGlucose()
            )
        );
        
        bmiColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(
                cellData.getValue().getBmi()
            )
        );
    }

    private void setupChartTypes() {
        chartTypeCombo.getItems().addAll(
            "Risk Trend",
            "Glucose Trend",
            "BMI Trend",
            "Blood Pressure Trend",
            "Insulin Trend"
        );
        chartTypeCombo.setValue("Risk Trend");
        chartTypeCombo.setOnAction(e -> refreshChart());
    }

    private void loadUserHistory() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            userHistory = historyDAO.getUserPredictionHistory(currentUser.getId());
            
            // Display summary
            if (!userHistory.isEmpty()) {
                PredictionHistory latest = userHistory.get(0);
                double avgRisk = userHistory.stream()
                    .mapToDouble(PredictionHistory::getRiskPercentage)
                    .average()
                    .orElse(0);
                
                double minGlucose = userHistory.stream()
                    .mapToDouble(PredictionHistory::getGlucose)
                    .min()
                    .orElse(0);
                
                double maxGlucose = userHistory.stream()
                    .mapToDouble(PredictionHistory::getGlucose)
                    .max()
                    .orElse(0);
                
                double avgGlucose = userHistory.stream()
                    .mapToDouble(PredictionHistory::getGlucose)
                    .average()
                    .orElse(0);
                
                summaryLabel.setText(String.format(
                    "Total Predictions: %d | Latest Risk: %.2f%% | Average Risk: %.2f%% | Glucose Range: %.0f - %.0f (Avg: %.0f)",
                    userHistory.size(),
                    latest.getRiskPercentage(),
                    avgRisk,
                    minGlucose,
                    maxGlucose,
                    avgGlucose
                ));
            }
            
            // Populate table
            historyTable.getItems().addAll(userHistory);
            
            // Initialize chart container
            initializeChartContainer();
            
            // Display initial chart
            if (!userHistory.isEmpty()) {
                refreshChart();
            }
            
            // Calculate and display statistics
            displayStatistics();
        } else {
            UIUtils.showError("Error", "No user logged in");
        }
    }

    private void initializeChartContainer() {
        // Find the Trends tab and get its VBox
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().contains("Trends")) {
                VBox content = (VBox) tab.getContent();
                // Create or find chart container
                chartContainer = new VBox();
                chartContainer.setPrefHeight(400);
                chartContainer.setStyle("-fx-padding: 10; -fx-border-color: #444444; -fx-border-width: 1;");
                
                // Add to the VBox after the combo box
                if (content.getChildren().size() > 1) {
                    content.getChildren().add(2, chartContainer);
                } else {
                    content.getChildren().add(chartContainer);
                }
                VBox.setVgrow(chartContainer, javafx.scene.layout.Priority.ALWAYS);
                break;
            }
        }
    }

    private void refreshChart() {
        if (userHistory == null || userHistory.isEmpty()) {
            UIUtils.showInfo("Info", "No prediction history available");
            return;
        }
        
        if (chartContainer == null) {
            return;
        }
        
        String chartType = chartTypeCombo.getValue();
        javafx.scene.chart.LineChart<Number, Number> chart = switch (chartType) {
            case "Risk Trend" -> SimpleChartGenerator.generateRiskTrendChart(userHistory);
            case "Glucose Trend" -> SimpleChartGenerator.generateGlucoseTrendChart(userHistory);
            case "BMI Trend" -> SimpleChartGenerator.generateBMITrendChart(userHistory);
            case "Blood Pressure Trend" -> SimpleChartGenerator.generateBloodPressureTrendChart(userHistory);
            case "Insulin Trend" -> SimpleChartGenerator.generateInsulinTrendChart(userHistory);
            default -> SimpleChartGenerator.generateRiskTrendChart(userHistory);
        };
        
        chart.setPrefHeight(400);
        chartContainer.getChildren().clear();
        chartContainer.getChildren().add(chart);
    }

    private void displayStatistics() {
        if (userHistory.isEmpty()) {
            return;
        }

        // Calculate statistics
        double minGlucose = userHistory.stream().mapToDouble(PredictionHistory::getGlucose).min().orElse(0);
        double maxGlucose = userHistory.stream().mapToDouble(PredictionHistory::getGlucose).max().orElse(0);
        double avgGlucose = userHistory.stream().mapToDouble(PredictionHistory::getGlucose).average().orElse(0);

        double minBmi = userHistory.stream().mapToDouble(PredictionHistory::getBmi).min().orElse(0);
        double maxBmi = userHistory.stream().mapToDouble(PredictionHistory::getBmi).max().orElse(0);
        double avgBmi = userHistory.stream().mapToDouble(PredictionHistory::getBmi).average().orElse(0);

        double minBp = userHistory.stream().mapToDouble(PredictionHistory::getBloodPressure).min().orElse(0);
        double maxBp = userHistory.stream().mapToDouble(PredictionHistory::getBloodPressure).max().orElse(0);
        double avgBp = userHistory.stream().mapToDouble(PredictionHistory::getBloodPressure).average().orElse(0);

        double minRisk = userHistory.stream().mapToDouble(PredictionHistory::getRiskPercentage).min().orElse(0);
        double maxRisk = userHistory.stream().mapToDouble(PredictionHistory::getRiskPercentage).max().orElse(0);
        double avgRisk = userHistory.stream().mapToDouble(PredictionHistory::getRiskPercentage).average().orElse(0);

        // Get the Statistics tab
        for (Tab tab : tabPane.getTabs()) {
            if (tab.getText().contains("Statistics")) {
                VBox statsContent = (VBox) tab.getContent();
                GridPane gridPane = (GridPane) statsContent.getChildren().stream()
                    .filter(node -> node instanceof GridPane)
                    .findFirst()
                    .orElse(null);

                if (gridPane != null) {
                    // Update all statistics boxes
                    updateAllStatisticsBoxes(gridPane, minGlucose, maxGlucose, avgGlucose,
                                            minBmi, maxBmi, avgBmi,
                                            minBp, maxBp, avgBp,
                                            minRisk, maxRisk, avgRisk);
                }
                break;
            }
        }
    }

    private void updateAllStatisticsBoxes(GridPane gridPane, double minGlucose, double maxGlucose, double avgGlucose,
                                          double minBmi, double maxBmi, double avgBmi,
                                          double minBp, double maxBp, double avgBp,
                                          double minRisk, double maxRisk, double avgRisk) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            if (node instanceof VBox box) {
                Integer colIndex = GridPane.getColumnIndex(node);
                Integer rowIndex = GridPane.getRowIndex(node);
                
                colIndex = (colIndex == null) ? 0 : colIndex;
                rowIndex = (rowIndex == null) ? 0 : rowIndex;

                if (box.getChildren().size() >= 4) {
                    Label minLabel = (Label) box.getChildren().get(1);
                    Label maxLabel = (Label) box.getChildren().get(2);
                    Label avgLabel = (Label) box.getChildren().get(3);

                    if (colIndex == 0 && rowIndex == 0) {
                        // Glucose Stats
                        minLabel.setText(String.format("Min: %.2f mg/dL", minGlucose));
                        maxLabel.setText(String.format("Max: %.2f mg/dL", maxGlucose));
                        avgLabel.setText(String.format("Avg: %.2f mg/dL", avgGlucose));
                    } else if (colIndex == 1 && rowIndex == 0) {
                        // BMI Stats
                        minLabel.setText(String.format("Min: %.2f kg/m²", minBmi));
                        maxLabel.setText(String.format("Max: %.2f kg/m²", maxBmi));
                        avgLabel.setText(String.format("Avg: %.2f kg/m²", avgBmi));
                    } else if (colIndex == 0 && rowIndex == 1) {
                        // Blood Pressure Stats
                        minLabel.setText(String.format("Min: %.2f mmHg", minBp));
                        maxLabel.setText(String.format("Max: %.2f mmHg", maxBp));
                        avgLabel.setText(String.format("Avg: %.2f mmHg", avgBp));
                    } else if (colIndex == 1 && rowIndex == 1) {
                        // Risk Stats
                        minLabel.setText(String.format("Min: %.2f %%", minRisk));
                        maxLabel.setText(String.format("Max: %.2f %%", maxRisk));
                        avgLabel.setText(String.format("Avg: %.2f %%", avgRisk));
                    }
                }
            }
        }
    }

    @FXML
    private void handleDownloadReport() {
        if (currentUser == null || userHistory.isEmpty()) {
            UIUtils.showError("Error", "No data to export");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Health Report");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        fileChooser.setInitialFileName("health_report_" + System.currentTimeMillis() + ".pdf");
        
        File selectedFile = fileChooser.showSaveDialog(backBtn.getScene().getWindow());
        if (selectedFile != null) {
            PDFReportGenerator.generateHealthReport(currentUser, userHistory, selectedFile.getAbsolutePath());
            UIUtils.showInfo("Success", "Report downloaded successfully to:\n" + selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleBack() {
        try {
            UIUtils.switchScene(backBtn, "patient_dashboard.fxml", "Patient Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError("Navigation Error", String.valueOf(e));
        }
    }

    @FXML
    private void handleFilter() {
        try {
            System.out.println("Filter button clicked");

            // Check if data is loaded
            if (userHistory == null || userHistory.isEmpty()) {
                UIUtils.showError("No Data", "No prediction history available to filter.");
                return;
            }

            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            System.out.println("Start Date: " + startDate);
            System.out.println("End Date: " + endDate);

            if (startDate == null && endDate == null) {
                UIUtils.showInfo("Filter Required", "Please select at least one date (start or end) to filter.");
                return;
            }

            if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
                UIUtils.showError("Invalid Date Range", "Start date cannot be after end date.");
                return;
            }

            // Filter the history list
            List<PredictionHistory> filteredHistory = userHistory.stream()
                .filter(history -> {
                    LocalDate predictionDate = history.getPredictionDate().toLocalDate();
                    boolean afterStart = (startDate == null) || !predictionDate.isBefore(startDate);
                    boolean beforeEnd = (endDate == null) || !predictionDate.isAfter(endDate);
                    return afterStart && beforeEnd;
                })
                .collect(Collectors.toList());

            System.out.println("Filtered results: " + filteredHistory.size() + " out of " + userHistory.size());

            // Update the table with filtered results
            historyTable.getItems().clear();
            historyTable.getItems().addAll(filteredHistory);

            // Update summary for filtered results
            updateSummaryLabel(filteredHistory);

            if (filteredHistory.isEmpty()) {
                UIUtils.showInfo("No Results", "No prediction history found in the selected date range.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError("Filter Error", "An error occurred while filtering: " + e.getMessage());
        }
    }

    @FXML
    private void handleClearFilter() {
        try {
            System.out.println("Clear filter button clicked");

            // Check if data is loaded
            if (userHistory == null || userHistory.isEmpty()) {
                UIUtils.showError("No Data", "No prediction history available.");
                return;
            }

            // Clear date pickers
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);

            // Restore full history to table
            historyTable.getItems().clear();
            historyTable.getItems().addAll(userHistory);

            // Update summary for all data
            updateSummaryLabel(userHistory);

            System.out.println("Filter cleared. Showing all " + userHistory.size() + " records.");
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showError("Clear Filter Error", "An error occurred while clearing filter: " + e.getMessage());
        }
    }

    private void updateSummaryLabel(List<PredictionHistory> historyList) {
        if (historyList.isEmpty()) {
            summaryLabel.setText("No data available");
            return;
        }

        PredictionHistory latest = historyList.get(0);
        double avgRisk = historyList.stream()
            .mapToDouble(PredictionHistory::getRiskPercentage)
            .average()
            .orElse(0);

        double minGlucose = historyList.stream()
            .mapToDouble(PredictionHistory::getGlucose)
            .min()
            .orElse(0);

        double maxGlucose = historyList.stream()
            .mapToDouble(PredictionHistory::getGlucose)
            .max()
            .orElse(0);

        double avgGlucose = historyList.stream()
            .mapToDouble(PredictionHistory::getGlucose)
            .average()
            .orElse(0);

        summaryLabel.setText(String.format(
            "Total Predictions: %d | Latest Risk: %.2f%% | Average Risk: %.2f%% | Glucose Range: %.0f - %.0f (Avg: %.0f)",
            historyList.size(),
            latest.getRiskPercentage(),
            avgRisk,
            minGlucose,
            maxGlucose,
            avgGlucose
        ));
    }
}
