package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.model.Paper;

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
                FOREIGN KEY (accessory_id) REFERENCES accessory(id) ON DELETE CASCADE
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public Paper insert(Paper paper) throws SQLException {
        String sql = "INSERT INTO paper (accessory_id, material) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, paper.getId());
            ps.setString(2, paper.getMaterial());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) paper.setId(rs.getInt(1));
            }
        }
        return paper;
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM paper WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
