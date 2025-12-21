package com.example.diabetes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    public Appointment create(Appointment a) throws SQLException {
        String sql = """
            INSERT INTO appointments(patient_user_id,doctor_user_id,scheduled_at,status,patient_note,doctor_note)
            VALUES(?,?,?,?,?,?)
        """;
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, a.getPatientUserId());
            ps.setInt(2, a.getDoctorUserId());
            ps.setString(3, a.getScheduledAt());
            ps.setString(4, a.getStatus());
            ps.setString(5, a.getPatientNote());
            ps.setString(6, a.getDoctorNote());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getInt(1));
            }
            return a;
        }
    }

    public List<Appointment> findByPatientId(int patientUserId) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE patient_user_id=? ORDER BY id DESC";
        return list(sql, patientUserId);
    }

    public List<Appointment> findByDoctorId(int doctorUserId) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE doctor_user_id=? ORDER BY id DESC";
        return list(sql, doctorUserId);
    }

    public List<Appointment> findByDoctorIdAndStatus(int doctorUserId, String status) throws SQLException {
        String sql = "SELECT * FROM appointments WHERE doctor_user_id=? AND status=? ORDER BY id DESC";
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, doctorUserId);
            ps.setString(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                List<Appointment> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        }
    }

    public void updateStatus(int appointmentId, String status, String scheduledAt, String doctorNote) throws SQLException {
        String sql = "UPDATE appointments SET status=?, scheduled_at=?, doctor_note=? WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, scheduledAt);
            ps.setString(3, doctorNote);
            ps.setInt(4, appointmentId);
            ps.executeUpdate();
        }
    }

    private List<Appointment> list(String sql, int id) throws SQLException {
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                List<Appointment> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        }
    }

    private Appointment map(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setId(rs.getInt("id"));
        a.setPatientUserId(rs.getInt("patient_user_id"));
        a.setDoctorUserId(rs.getInt("doctor_user_id"));
        a.setScheduledAt(rs.getString("scheduled_at"));
        a.setStatus(rs.getString("status"));
        a.setPatientNote(rs.getString("patient_note"));
        a.setDoctorNote(rs.getString("doctor_note"));
        return a;
    }
}
