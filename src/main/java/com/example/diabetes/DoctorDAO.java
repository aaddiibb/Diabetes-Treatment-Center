package com.example.diabetes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    public void createDoctorProfile(int userId, String specialty, String clinic, String days) throws SQLException {
        String sql = "INSERT INTO doctors(user_id,specialty,clinic_address,available_days) VALUES(?,?,?,?)";
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, specialty);
            ps.setString(3, clinic);
            ps.setString(4, days);
            ps.executeUpdate();
        }
    }

    public List<Doctor> findAll() throws SQLException {
        String sql = """
            SELECT d.id AS did, d.user_id, d.specialty, d.clinic_address, d.available_days,
                   u.name, u.email
            FROM doctors d
            JOIN users u ON u.id = d.user_id
            ORDER BY u.name
        """;

        List<Doctor> out = new ArrayList<>();
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(map(rs));
        }
        return out;
    }

    public Doctor findByUserId(int doctorUserId) throws SQLException {
        String sql = """
            SELECT d.id AS did, d.user_id, d.specialty, d.clinic_address, d.available_days,
                   u.name, u.email
            FROM doctors d
            JOIN users u ON u.id = d.user_id
            WHERE d.user_id = ?
        """;
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, doctorUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        }
    }

    private Doctor map(ResultSet rs) throws SQLException {
        Doctor d = new Doctor();
        d.setId(rs.getInt("did"));
        d.setUserId(rs.getInt("user_id"));
        d.setSpecialty(rs.getString("specialty"));
        d.setClinicAddress(rs.getString("clinic_address"));
        d.setAvailableDays(rs.getString("available_days"));
        d.setName(rs.getString("name"));
        d.setEmail(rs.getString("email"));
        return d;
    }
}
