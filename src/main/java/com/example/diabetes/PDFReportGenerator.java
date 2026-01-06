package com.example.diabetes;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFReportGenerator {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

    public static void generateHealthReport(User user, List<PredictionHistory> history, String filePath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Add title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Health Report - Diabetes Prediction", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Add date
            Font dateFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
            Paragraph dateP = new Paragraph("Report Generated: " + java.time.LocalDateTime.now().format(dateFormatter), dateFont);
            dateP.setAlignment(Element.ALIGN_CENTER);
            document.add(dateP);

            document.add(new Paragraph("\n"));

            // Add patient info
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.DARK_GRAY);
            document.add(new Paragraph("Patient Information", headerFont));

            PdfPTable patientTable = new PdfPTable(2);
            patientTable.setWidthPercentage(100);
            addTableCell(patientTable, "Name:", user.getName(), false);
            addTableCell(patientTable, "Email:", user.getEmail(), true);
            addTableCell(patientTable, "Role:", user.getRole(), false);
            addTableCell(patientTable, "Phone:", user.getPhone() != null ? user.getPhone() : "N/A", true);
            document.add(patientTable);

            document.add(new Paragraph("\n"));

            // Add prediction summary
            if (!history.isEmpty()) {
                document.add(new Paragraph("Prediction Summary", headerFont));

                PredictionHistory latest = history.get(0);
                PdfPTable summaryTable = new PdfPTable(2);
                summaryTable.setWidthPercentage(100);
                addTableCell(summaryTable, "Latest Risk Percentage:", String.format("%.2f%%", latest.getRiskPercentage()), false);
                addTableCell(summaryTable, "Risk Level:", latest.getRiskLevel(), true);
                addTableCell(summaryTable, "Date:", latest.getPredictionDate().format(dateFormatter), false);
                addTableCell(summaryTable, "Total Predictions:", history.size() + "", true);
                document.add(summaryTable);

                document.add(new Paragraph("\n"));

                // Add health metrics
                document.add(new Paragraph("Current Health Metrics", headerFont));

                PdfPTable metricsTable = new PdfPTable(2);
                metricsTable.setWidthPercentage(100);
                addTableCell(metricsTable, "Glucose (mg/dL):", String.format("%.2f", latest.getGlucose()), false);
                addTableCell(metricsTable, "Blood Pressure (mmHg):", String.format("%.2f", latest.getBloodPressure()), true);
                addTableCell(metricsTable, "BMI (kg/m²):", String.format("%.2f", latest.getBmi()), false);
                addTableCell(metricsTable, "Insulin (μU/ml):", String.format("%.2f", latest.getInsulin()), true);
                addTableCell(metricsTable, "Age:", latest.getAge() + "", false);
                addTableCell(metricsTable, "Pregnancies:", latest.getPregnancies() + "", true);
                addTableCell(metricsTable, "Skin Thickness (mm):", String.format("%.2f", latest.getSkinThickness()), false);
                addTableCell(metricsTable, "DPF:", String.format("%.4f", latest.getDpf()), true);
                document.add(metricsTable);

                document.add(new Paragraph("\n"));

                // Add prediction history table
                document.add(new Paragraph("Prediction History (Last 10)", headerFont));

                PdfPTable historyTable = new PdfPTable(5);
                historyTable.setWidthPercentage(100);

                // Header row
                PdfPCell headerCell = new PdfPCell(new Phrase("Date", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE)));
                headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
                historyTable.addCell(headerCell);

                headerCell = new PdfPCell(new Phrase("Risk %", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE)));
                headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
                historyTable.addCell(headerCell);

                headerCell = new PdfPCell(new Phrase("Risk Level", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE)));
                headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
                historyTable.addCell(headerCell);

                headerCell = new PdfPCell(new Phrase("Glucose", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE)));
                headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
                historyTable.addCell(headerCell);

                headerCell = new PdfPCell(new Phrase("BMI", new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE)));
                headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
                historyTable.addCell(headerCell);

                // Data rows (limit to last 10)
                int limit = Math.min(10, history.size());
                for (int i = 0; i < limit; i++) {
                    PredictionHistory pred = history.get(i);
                    historyTable.addCell(pred.getPredictionDate().format(dateFormatter));
                    historyTable.addCell(String.format("%.2f%%", pred.getRiskPercentage()));
                    historyTable.addCell(pred.getRiskLevel());
                    historyTable.addCell(String.format("%.2f", pred.getGlucose()));
                    historyTable.addCell(String.format("%.2f", pred.getBmi()));
                }

                document.add(historyTable);

                document.add(new Paragraph("\n"));

                // Add recommendations
                document.add(new Paragraph("Recommendations", headerFont));
                Font bodyFont = new Font(Font.FontFamily.HELVETICA, 11);
                if (latest.getRiskLevel().equals("HIGH RISK")) {
                    document.add(new Paragraph("⚠ HIGH RISK DETECTED", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.RED)));
                    document.add(new Paragraph("Please consult with a healthcare professional immediately.", bodyFont));
                    document.add(new Paragraph("Monitor your blood glucose regularly and consider lifestyle modifications.", bodyFont));
                } else {
                    document.add(new Paragraph("✓ LOW RISK", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.GREEN)));
                    document.add(new Paragraph("Your risk is relatively low. Continue maintaining a healthy lifestyle with regular exercise and balanced nutrition.", bodyFont));
                    document.add(new Paragraph("Regular check-ups are recommended.", bodyFont));
                }
            }

            document.close();
            System.out.println("PDF Report generated successfully: " + filePath);

        } catch (DocumentException | IOException e) {
            System.err.println("Error generating PDF report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void addTableCell(PdfPTable table, String label, String value, boolean alternate) {
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
        Font valueFont = new Font(Font.FontFamily.HELVETICA, 11);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));

        if (alternate) {
            labelCell.setBackgroundColor(new BaseColor(240, 240, 240));
            valueCell.setBackgroundColor(new BaseColor(240, 240, 240));
        }

        labelCell.setPadding(8);
        valueCell.setPadding(8);
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}
