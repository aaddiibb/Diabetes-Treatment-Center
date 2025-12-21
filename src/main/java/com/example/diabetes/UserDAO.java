package com.example.diabetes;

import java.sql.*;

public class UserDAO {

    public User create(User user) throws SQLException {
        String sql = "INSERT INTO users(role,name,email,phone,password_hash) VALUES(?,?,?,?,?)";
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getRole());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getPasswordHash());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) user.setId(keys.getInt(1));
            }
            return user;
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email=?";
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        }
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id=?";
        try (Connection c = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        }
    }

    public User authenticate(String email, String password) throws SQLException {
        User u = findByEmail(email);
        System.out.println("DEBUG UserDAO: findByEmail returned: " + (u != null ? u.getName() : "null"));
        if (u == null) return null;
        boolean passwordMatch = PasswordUtils.verify(password, u.getPasswordHash());
        System.out.println("DEBUG UserDAO: Password verification result: " + passwordMatch);
        return passwordMatch ? u : null;
    }

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setRole(rs.getString("role"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setPasswordHash(rs.getString("password_hash"));
        return u;
    }
}
