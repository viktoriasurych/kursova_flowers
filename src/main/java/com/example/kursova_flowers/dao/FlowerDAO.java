package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.model.Flower;
import com.example.kursova_flowers.model.FlowerType;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
                FOREIGN KEY (type_id) REFERENCES flower_type(id)
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public List<Flower> findByType(FlowerType type) throws SQLException {
        String sql = "SELECT * FROM flower WHERE type_id = ?";
        List<Flower> flowers = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, type.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Flower flower = new Flower();
                flower.setId(rs.getInt("id"));
                flower.setName(rs.getString("name"));
                flower.setPrice(rs.getDouble("price"));

                String dateStr = rs.getString("picked_date");
                if (dateStr != null) {
                    flower.setPickedDate(LocalDate.parse(dateStr));
                }



                flower.setType(type);
                flowers.add(flower);
            }
        }
        return flowers;
    }



    public void insert(Flower flower) throws SQLException {
        String sql = "INSERT INTO flower (name, price, picked_date, type_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, flower.getName());
            stmt.setDouble(2, flower.getPrice());
            stmt.setString(4, flower.getPickedDate().toString()); // формат yyyy-MM-dd

            stmt.setInt(5, flower.getType().getId());

            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                flower.setId(generatedKeys.getInt(1));
            }
        }
    }

    public void update(Flower flower) throws SQLException {
        String sql = "UPDATE flower SET name = ?, price = ?, picked_date = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, flower.getName());
            stmt.setDouble(2, flower.getPrice());
            stmt.setString(4, flower.getPickedDate().toString()); // формат yyyy-MM-dd

            stmt.setInt(5, flower.getId());

            stmt.executeUpdate();
        }
    }

    public void delete(Flower flower) throws SQLException {
        String sql = "DELETE FROM flower WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, flower.getId());
            stmt.executeUpdate();
        }
    }

}
