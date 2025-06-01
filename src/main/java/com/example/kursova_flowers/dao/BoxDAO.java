package com.example.kursova_flowers.dao;
import java.sql.*;

public class BoxDAO {
    private final Connection connection;

    public BoxDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS box (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                accessory_id INTEGER UNIQUE NOT NULL,
                style TEXT,
                FOREIGN KEY (accessory_id) REFERENCES accessory(id)
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

}
