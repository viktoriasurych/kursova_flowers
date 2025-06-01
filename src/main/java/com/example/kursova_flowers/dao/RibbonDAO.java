package com.example.kursova_flowers.dao;
import java.sql.*;

public class RibbonDAO {
    private final Connection connection;

    public RibbonDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS ribbon (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                accessory_id INTEGER UNIQUE NOT NULL,
                width REAL,
                FOREIGN KEY (accessory_id) REFERENCES accessory(id)
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
}
