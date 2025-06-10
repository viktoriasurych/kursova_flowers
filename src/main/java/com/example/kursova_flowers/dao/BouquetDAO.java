package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.model.Bouquet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BouquetDAO {
    private final Connection connection;

    public BouquetDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS bouquet (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public Bouquet insert(Bouquet bouquet) throws SQLException {
        String sql = "INSERT INTO bouquet (name) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, bouquet.getName());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    bouquet.setId(rs.getInt(1));
                }
            }
        }
        return bouquet;
    }

    public List<Bouquet> findAll() throws SQLException {

            List<Bouquet> bouquets = new ArrayList<>();
            String sql = "SELECT * FROM bouquet  ORDER BY id DESC";

            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Bouquet bouquet = new Bouquet();
                    bouquet.setId(rs.getInt("id"));
                    bouquet.setName(rs.getString("name"));

                    // завантажити квіти та аксесуари
                    FlowerInBouquetDAO flowerDAO = new FlowerInBouquetDAO(connection);
                    bouquet.setFlowers(flowerDAO.findByBouquetId(bouquet.getId()));

                    AccessoryDAO accessoryDAO = new AccessoryDAO(connection);
                    bouquet.setAccessories(accessoryDAO.findByBouquetId(bouquet.getId()));

                    bouquets.add(bouquet);
                }
            }

            return bouquets;


    }

    public void update(Bouquet bouquet) throws SQLException {
        String sql = "UPDATE bouquet SET name = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, bouquet.getName());
            stmt.setInt(2, bouquet.getId());
            stmt.executeUpdate();
        }
    }


    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM bouquet WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
