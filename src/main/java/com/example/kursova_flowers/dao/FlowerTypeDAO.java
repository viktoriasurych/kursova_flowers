package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.model.FlowerType;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FlowerTypeDAO {
    private final Connection connection;

    public FlowerTypeDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS flower_type (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void insert(FlowerType type) throws SQLException {
        String sql = "INSERT INTO flower_type (name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, type.getName());
            pstmt.executeUpdate();
        }
    }
}
