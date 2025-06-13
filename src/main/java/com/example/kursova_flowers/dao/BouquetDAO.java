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
        String sql = "INSERT INTO bouquet(name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, bouquet.getName());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating bouquet failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bouquet.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating bouquet failed, no ID obtained.");
                }
            }
        }
        return bouquet;
    }

    public void update(Bouquet bouquet) throws SQLException {
        String sql = "UPDATE bouquet SET name = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bouquet.getName());
            pstmt.setInt(2, bouquet.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM bouquet WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
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

                FlowerInBouquetDAO flowerDAO = new FlowerInBouquetDAO(connection);
                bouquet.setFlowers(flowerDAO.findByBouquetId(bouquet.getId()));

                AccessoryDAO accessoryDAO = new AccessoryDAO(connection);
                bouquet.setAccessories(accessoryDAO.findByBouquetId(bouquet.getId()));

                bouquets.add(bouquet);
            }
        }

        return bouquets;

    }
}
