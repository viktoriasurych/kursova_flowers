package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.db.DatabaseConnection;
import com.example.kursova_flowers.model.Flower;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlowerDAO {
    private final Connection connection;

    public FlowerDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS flower (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                type_id INTEGER,
                price REAL NOT NULL,
                picked_date TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                FOREIGN KEY (type_id) REFERENCES flower_type(id)
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
}
