package com.example.kursova_flowers.dao;
import java.sql.*;

public class AccessoryDAO {
    private final Connection connection;

    public AccessoryDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS accessory (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                bouquet_id INTEGER NOT NULL,
                type_id INTEGER NOT NULL,
                color TEXT,
                custom_text TEXT,
                FOREIGN KEY (bouquet_id) REFERENCES bouquet(id),
                FOREIGN KEY (type_id) REFERENCES accessory_type(id)
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
}
