package com.example.diabetes;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:diabetes_app.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {}

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    public synchronized void initialize() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            createSchema();
        }
    }

    private void createSchema() throws SQLException {
        String users = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    role TEXT NOT NULL CHECK(role IN ('PATIENT','DOCTOR')),
                    name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    phone TEXT,
                    password_hash TEXT NOT NULL
                );
                """;

        String doctors = """
                CREATE TABLE IF NOT EXISTS doctors (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL UNIQUE,
                    specialty TEXT NOT NULL,
                    clinic_address TEXT,
                    available_days TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                );
                """;

        String appointments = """
                CREATE TABLE IF NOT EXISTS appointments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patient_user_id INTEGER NOT NULL,
                    doctor_user_id INTEGER NOT NULL,
                    scheduled_at TEXT,
                    status TEXT NOT NULL DEFAULT 'REQUESTED'
                        CHECK(status IN ('REQUESTED','SCHEDULED','COMPLETED','CANCELLED')),
                    patient_note TEXT,
                    doctor_note TEXT,
                    FOREIGN KEY (patient_user_id) REFERENCES users(id) ON DELETE CASCADE,
                    FOREIGN KEY (doctor_user_id) REFERENCES users(id) ON DELETE CASCADE
                );
                """;

        String predictionHistory = """
                CREATE TABLE IF NOT EXISTS prediction_history (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    prediction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    risk_percentage REAL NOT NULL,
                    risk_level TEXT NOT NULL,
                    pregnancies INTEGER,
                    glucose REAL,
                    blood_pressure REAL,
                    skin_thickness REAL,
                    insulin REAL,
                    bmi REAL,
                    dpf REAL,
                    age INTEGER,
                    notes TEXT,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                );
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(users);
            stmt.execute(doctors);
            stmt.execute(appointments);
            stmt.execute(predictionHistory);
            
            // Only seed if this is a new database (no users exist)
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS c FROM users")) {
                if (rs.next() && rs.getInt("c") == 0) {
                    // No initial seeding - let users register their own accounts
                }
            }
        }
    }

    private void seedDemoDoctors(Statement stmt) throws SQLException {
        // No demo doctors - users will register their own doctor accounts
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) initialize();
        return connection;
    }

    public synchronized void close() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException ignored) {}
    }
}
