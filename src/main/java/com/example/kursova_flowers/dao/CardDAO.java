package com.example.kursova_flowers.dao;
import com.example.kursova_flowers.model.GreetingCard;
import com.example.kursova_flowers.model.Paper;

import java.sql.*;

public class CardDAO {
    private final Connection connection;

    public CardDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS card (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                accessory_id INTEGER UNIQUE NOT NULL,
                card_text TEXT,
                FOREIGN KEY (accessory_id) REFERENCES accessory(id) ON DELETE CASCADE
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    /** Вставляє новий запис і повертає об’єкт з оновленим id */
    public GreetingCard insert(GreetingCard card) throws SQLException {
        String sql = "INSERT INTO card (accessory_id, card_text) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, card.getId());
            ps.setString(2, card.getText());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) card.setId(rs.getInt(1));
            }
        }
        return card;
    }

    /** Видаляє запис за власним id */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM card WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
