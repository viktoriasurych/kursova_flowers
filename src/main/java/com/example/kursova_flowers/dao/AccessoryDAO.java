package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.model.Accessory;
import com.example.kursova_flowers.model.AccessoryType;
import com.example.kursova_flowers.model.Bouquet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccessoryDAO {
    private final Connection connection;

    public AccessoryDAO(Connection connection) {
        this.connection = connection;
    }

    public void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS accessory (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                bouquet_id INTEGER NOT NULL,
                accessory_type_id INTEGER NOT NULL,
                color TEXT,
                note TEXT,
                FOREIGN KEY(bouquet_id) REFERENCES bouquet(id),
                FOREIGN KEY(accessory_type_id) REFERENCES accessory_type(id)
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public Accessory insert(Accessory accessory) throws SQLException {
        String sql = "INSERT INTO accessory (bouquet_id, type_id, color, custom_text) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, accessory.getBouquet().getId());
            ps.setInt(2, accessory.getType().getId());
            ps.setString(3, accessory.getColor());
            ps.setString(4, accessory.getNote());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    accessory.setId(rs.getInt(1));
                }
            }
        }
        return accessory;
    }

    public List<Accessory> findByBouquetId(int bouquetId) throws SQLException {
        List<Accessory> list = new ArrayList<>();
        String sql = """
            SELECT a.id, a.color, a.note,
                   at.id as accessory_type_id, at.name as accessory_type_name, at.base_price as accessory_type_price
            FROM accessory a
            JOIN accessory_type at ON a.accessory_type_id = at.id
            WHERE a.bouquet_id = ?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bouquetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Accessory accessory = new Accessory();
                    accessory.setId(rs.getInt("id"));

                    AccessoryType type = new AccessoryType();
                    type.setId(rs.getInt("accessory_type_id"));
                    type.setName(rs.getString("accessory_type_name"));
                    type.setBasePrice(rs.getDouble("accessory_type_price"));
                    accessory.setType(type);

                    Bouquet bouquet = new Bouquet();
                    bouquet.setId(bouquetId);
                    accessory.setBouquet(bouquet);

                    accessory.setColor(rs.getString("color"));
                    accessory.setNote(rs.getString("note"));

                    list.add(accessory);
                }
            }
        }
        return list;
    }
}
