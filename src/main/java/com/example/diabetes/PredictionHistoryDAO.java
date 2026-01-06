package com.example.diabetes;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PredictionHistoryDAO {
    private final DatabaseManager dbManager = DatabaseManager.getInstance();

    public void savePrediction(PredictionHistory history) {
        String sql = """
                INSERT INTO prediction_history (user_id, prediction_date, risk_percentage, risk_level,
                    pregnancies, glucose, blood_pressure, skin_thickness, insulin, bmi, dpf, age, notes)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, history.getUserId());
            pstmt.setString(2, history.getPredictionDate().toString());
            pstmt.setDouble(3, history.getRiskPercentage());
            pstmt.setString(4, history.getRiskLevel());
            pstmt.setInt(5, history.getPregnancies());
            pstmt.setDouble(6, history.getGlucose());
            pstmt.setDouble(7, history.getBloodPressure());
            pstmt.setDouble(8, history.getSkinThickness());
            pstmt.setDouble(9, history.getInsulin());
            pstmt.setDouble(10, history.getBmi());
            pstmt.setDouble(11, history.getDpf());
            pstmt.setInt(12, history.getAge());
            pstmt.setString(13, history.getNotes());

            pstmt.executeUpdate();
            System.out.println("Prediction saved to history");
        } catch (SQLException e) {
            System.err.println("Error saving prediction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<PredictionHistory> getUserPredictionHistory(int userId) {
        List<PredictionHistory> history = new ArrayList<>();
        String sql = """
                SELECT * FROM prediction_history
                WHERE user_id = ?
                ORDER BY prediction_date DESC
                """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PredictionHistory pred = new PredictionHistory(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        LocalDateTime.parse(rs.getString("prediction_date")),
                        rs.getDouble("risk_percentage"),
                        rs.getString("risk_level"),
                        rs.getInt("pregnancies"),
                        rs.getDouble("glucose"),
                        rs.getDouble("blood_pressure"),
                        rs.getDouble("skin_thickness"),
                        rs.getDouble("insulin"),
                        rs.getDouble("bmi"),
                        rs.getDouble("dpf"),
                        rs.getInt("age"),
                        rs.getString("notes")
                );
                history.add(pred);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving prediction history: " + e.getMessage());
            e.printStackTrace();
        }

        return history;
    }

    public PredictionHistory getPredictionById(int id) {
        String sql = "SELECT * FROM prediction_history WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new PredictionHistory(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        LocalDateTime.parse(rs.getString("prediction_date")),
                        rs.getDouble("risk_percentage"),
                        rs.getString("risk_level"),
                        rs.getInt("pregnancies"),
                        rs.getDouble("glucose"),
                        rs.getDouble("blood_pressure"),
                        rs.getDouble("skin_thickness"),
                        rs.getDouble("insulin"),
                        rs.getDouble("bmi"),
                        rs.getDouble("dpf"),
                        rs.getInt("age"),
                        rs.getString("notes")
                );
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving prediction: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public void updatePredictionNotes(int predictionId, String notes) {
        String sql = "UPDATE prediction_history SET notes = ? WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, notes);
            pstmt.setInt(2, predictionId);
            pstmt.executeUpdate();
            System.out.println("Prediction notes updated");
        } catch (SQLException e) {
            System.err.println("Error updating prediction notes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deletePrediction(int predictionId) {
        String sql = "DELETE FROM prediction_history WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, predictionId);
            pstmt.executeUpdate();
            System.out.println("Prediction deleted");
        } catch (SQLException e) {
            System.err.println("Error deleting prediction: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<PredictionHistory> getUserPredictionHistoryByDateRange(int userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<PredictionHistory> history = new ArrayList<>();
        String sql = """
                SELECT * FROM prediction_history
                WHERE user_id = ? AND prediction_date BETWEEN ? AND ?
                ORDER BY prediction_date DESC
                """;

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, startDate.toString());
            pstmt.setString(3, endDate.toString());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PredictionHistory pred = new PredictionHistory(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        LocalDateTime.parse(rs.getString("prediction_date")),
                        rs.getDouble("risk_percentage"),
                        rs.getString("risk_level"),
                        rs.getInt("pregnancies"),
                        rs.getDouble("glucose"),
                        rs.getDouble("blood_pressure"),
                        rs.getDouble("skin_thickness"),
                        rs.getDouble("insulin"),
                        rs.getDouble("bmi"),
                        rs.getDouble("dpf"),
                        rs.getInt("age"),
                        rs.getString("notes")
                );
                history.add(pred);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error retrieving prediction history by date range: " + e.getMessage());
            e.printStackTrace();
        }

        return history;
    }
}
