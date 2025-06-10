package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.model.FlowerType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    // Видалення квітки за id
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM flower_type WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // Отримати всі типи квітів (для відображення в таблиці)
    public List<FlowerType> findAll() throws SQLException {
        List<FlowerType> list = new ArrayList<>();
        String sql = "SELECT id, name FROM flower_type ORDER BY name";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                FlowerType type = new FlowerType(rs.getInt("id"), rs.getString("name"));
                list.add(type);
            }
        }
        return list;
    }
}
