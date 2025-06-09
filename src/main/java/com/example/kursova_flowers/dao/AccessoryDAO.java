package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.model.Accessory;
import com.example.kursova_flowers.model.AccessoryType;
import com.example.kursova_flowers.model.Bouquet;
import com.example.kursova_flowers.model.*;

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
                FOREIGN KEY(bouquet_id) REFERENCES bouquet(id) ON DELETE CASCADE,
                FOREIGN KEY(accessory_type_id) REFERENCES accessory_type(id)
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public Accessory insert(Accessory accessory) throws SQLException {
        String sql = "INSERT INTO accessory (bouquet_id, accessory_type_id, color, note) VALUES (?, ?, ?, ?)";
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

   /* public List<Accessory> findByBouquetId(int bouquetId) throws SQLException {
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
    }*/

    public List<Accessory> findByBouquetId(int bouquetId) throws SQLException {
        List<Accessory> list = new ArrayList<>();
        String sql = """
        SELECT a.id, a.color, a.note, a.accessory_type_id,
               at.name AS accessory_type_name, at.base_price AS accessory_type_price,
               b.style,
               r.width,
               p.material,
               g.card_text
        FROM accessory a
        JOIN accessory_type at ON a.accessory_type_id = at.id
        LEFT JOIN box b ON a.id = b.accessory_id
        LEFT JOIN ribbon r ON a.id = r.accessory_id
        LEFT JOIN paper p ON a.id = p.accessory_id
        LEFT JOIN card g ON a.id = g.accessory_id
        WHERE a.bouquet_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, bouquetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int typeId = rs.getInt("accessory_type_id");
                    Accessory accessory;

                    switch (typeId) {
                        case 2: // Box
                            Box box = new Box();
                            box.setBoxType(rs.getString("style"));
                            accessory = box;
                            break;
                        case 3: // Ribbon
                            Ribbon ribbon = new Ribbon();
                            ribbon.setWidth(rs.getInt("width"));
                            accessory = ribbon;
                            break;
                        case 4: // Paper
                            Paper paper = new Paper();
                            paper.setMaterial(rs.getString("material"));
                            accessory = paper;
                            break;
                        case 1: // GreetingCard
                            GreetingCard card = new GreetingCard();
                            card.setText(rs.getString("card_text"));
                            accessory = card;
                            break;
                        default:
                            accessory = new Accessory();
                            break;
                    }

                    accessory.setId(rs.getInt("id"));
                    AccessoryType type = new AccessoryType();
                    type.setId(typeId);
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


    public void deleteByBouquetId(int bouquetId) throws SQLException {
        String sql = "DELETE FROM accessory WHERE bouquet_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bouquetId);
            stmt.executeUpdate();
        }
    }
}
