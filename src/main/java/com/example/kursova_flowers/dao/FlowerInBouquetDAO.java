package com.example.kursova_flowers.dao;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FlowerInBouquetDAO {
    private final Connection connection;

    public FlowerInBouquetDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS flower_in_bouquet (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                flower_id INTEGER NOT NULL,
                bouquet_id INTEGER NOT NULL,
                stem_length REAL NOT NULL,
                quantity INTEGER NOT NULL,
                FOREIGN KEY (flower_id) REFERENCES flower(id),
                FOREIGN KEY (bouquet_id) REFERENCES bouquet(id)
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
}
