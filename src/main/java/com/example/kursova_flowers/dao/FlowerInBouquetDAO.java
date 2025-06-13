package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
            stem_length REAL,
            quantity INTEGER,
            FOREIGN KEY(flower_id) REFERENCES flower(id) ON DELETE CASCADE,
            FOREIGN KEY(bouquet_id) REFERENCES bouquet(id) ON DELETE CASCADE
        )
    """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public FlowerInBouquet insert(FlowerInBouquet fib) throws SQLException {
        String sql = "INSERT INTO flower_in_bouquet (flower_id, bouquet_id, stem_length, quantity) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, fib.getFlower().getId());
            ps.setInt(2, fib.getBouquet().getId());
            ps.setDouble(3, fib.getStemLength());
            ps.setInt(4, fib.getQuantity());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    fib.setId(rs.getInt(1));
                }
            }
        }
        return fib;
    }

    public List<FlowerInBouquet> findByBouquetId(int bouquetId) throws SQLException {
        List<FlowerInBouquet> list = new ArrayList<>();
        String sql = """
    SELECT fib.id, fib.stem_length, fib.quantity,
                           f.id as flower_id, f.name as flower_name, f.price as flower_price, f.picked_date,
                           ft.id as type_id, ft.name as type_name
                
    FROM flower_in_bouquet fib
    JOIN flower f ON fib.flower_id = f.id
    JOIN flower_type ft ON f.type_id = ft.id
    WHERE fib.bouquet_id = ?
""";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bouquetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FlowerInBouquet fib = new FlowerInBouquet();
                    fib.setId(rs.getInt("id"));

                    Flower flower = new Flower();
                    flower.setId(rs.getInt("flower_id"));
                    flower.setName(rs.getString("flower_name"));
                    flower.setPrice(rs.getDouble("flower_price"));
                    String dateStr = rs.getString("picked_date");
                    if (dateStr != null) {
                        flower.setPickedDate(LocalDate.parse(dateStr));
                    }

                    FlowerType type = new FlowerType();
                    type.setId(rs.getInt("type_id"));
                    type.setName(rs.getString("type_name"));
                    flower.setType(type);

                    fib.setFlower(flower);

                    Bouquet bouquet = new Bouquet();
                    bouquet.setId(bouquetId);
                    fib.setBouquet(bouquet);

                    fib.setStemLength(rs.getDouble("stem_length"));
                    fib.setQuantity(rs.getInt("quantity"));

                    list.add(fib);
                }
            }
        }

        return list;
    }

    public void deleteByBouquetId(int bouquetId) throws SQLException {
        String sql = "DELETE FROM flower_in_bouquet WHERE bouquet_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bouquetId);
            stmt.executeUpdate();
        }
    }

}
