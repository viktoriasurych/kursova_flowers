package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.model.Ribbon;

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
                FOREIGN KEY (accessory_id) REFERENCES accessory(id) ON DELETE CASCADE
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public Ribbon insert(Ribbon ribbon) throws SQLException {
        String sql = "INSERT INTO ribbon (accessory_id, width) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, ribbon.getId());
            ps.setDouble(2, ribbon.getWidth());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) ribbon.setId(rs.getInt(1));
            }
        }
        return ribbon;
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM ribbon WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

}
