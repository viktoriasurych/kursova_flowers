package com.example.kursova_flowers.dao;
import com.example.kursova_flowers.model.Box;

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

    /** Вставляє новий запис і повертає об’єкт з оновленим id */
    public Box insert(Box box) throws SQLException {
        String sql = "INSERT INTO box (accessory_id, style) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, box.getId());
            ps.setString(2, box.getBoxType());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) box.setId(rs.getInt(1));
            }
        }
        return box;
    }

    /** Оновлює поле style за accessory_id */
    public void update(Box box) throws SQLException {
        String sql = "UPDATE box SET style = ? WHERE accessory_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, box.getBoxType());
          //  ps.setInt(2, box.getAccessory().getId());
            ps.executeUpdate();
        }
    }

    /** Видаляє запис за власним id */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM box WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

}
