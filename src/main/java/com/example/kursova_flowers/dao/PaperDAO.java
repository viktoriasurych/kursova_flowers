package com.example.kursova_flowers.dao;
import java.sql.*;

public class PaperDAO {
    private final Connection connection;

    public PaperDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS paper (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                accessory_id INTEGER UNIQUE NOT NULL,
                material TEXT,
                FOREIGN KEY (accessory_id) REFERENCES accessory(id)
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
}
