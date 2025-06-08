package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.model.AccessoryType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoryTypeDAO {
    private final Connection connection;

    public AccessoryTypeDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS accessory_type (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE,
                base_price REAL NOT NULL
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    // Отримати всі аксесуари з таблиці
    public List<AccessoryType> findAll() throws SQLException {
        List<AccessoryType> list = new ArrayList<>();
        String sql = "SELECT id, name, base_price FROM accessory_type ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                AccessoryType at = new AccessoryType(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("base_price")
                );
                list.add(at);
            }
        }
        return list;
    }

    // Оновити ціну аксесуара за id
    public void updateBasePrice(int id, double newPrice) throws SQLException {
        String sql = "UPDATE accessory_type SET base_price = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, newPrice);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    // Опційно: вставити початкові аксесуари, якщо таблиця порожня
    public void insertDefaultAccessoriesIfEmpty() throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM accessory_type";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                String insertSql = "INSERT INTO accessory_type (name, base_price) VALUES (?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                    String[] names = {"листівка", "коробка", "стрічка", "папір"};
                    double defaultPrice = 0.0; // початкова ціна, можна змінити
                    for (String name : names) {
                        ps.setString(1, name);
                        ps.setDouble(2, defaultPrice);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        }
    }

    public AccessoryType findByName(String name) throws SQLException {
        String sql = "SELECT id, name, base_price FROM accessory_type WHERE LOWER(name) = LOWER(?) LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AccessoryType(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("base_price")
                    );
                }
            }
        }
        return null; // або кинь виняток, якщо не знайдено
    }

}
