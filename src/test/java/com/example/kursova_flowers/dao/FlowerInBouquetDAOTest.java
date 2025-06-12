package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.dao.FlowerInBouquetDAO;
import com.example.kursova_flowers.model.*;

import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlowerInBouquetDAOTest {

    private static Connection connection;
    private static FlowerInBouquetDAO dao;
    private static int bouquetId;
    private static int flowerId;
    private static int flowerTypeId;

    @BeforeAll
    static void setup() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        try (Statement stmt = connection.createStatement()) {
            // Створення необхідних таблиць для FK
            stmt.execute("CREATE TABLE bouquet (id INTEGER PRIMARY KEY AUTOINCREMENT)");
            stmt.execute("""
                CREATE TABLE flower_type (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT
                )
            """);
            stmt.execute("""
                CREATE TABLE flower (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT,
                    price REAL,
                    picked_date TEXT,
                    type_id INTEGER,
                    FOREIGN KEY(type_id) REFERENCES flower_type(id)
                )
            """);
        }

        dao = new FlowerInBouquetDAO(connection);
        dao.createTable();

        // Вставка даних в bouquet, flower_type, flower
        try (PreparedStatement psBouquet = connection.prepareStatement("INSERT INTO bouquet DEFAULT VALUES", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psType = connection.prepareStatement("INSERT INTO flower_type (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psFlower = connection.prepareStatement("INSERT INTO flower (name, price, picked_date, type_id) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            psBouquet.executeUpdate();
            try (ResultSet rs = psBouquet.getGeneratedKeys()) {
                assertTrue(rs.next());
                bouquetId = rs.getInt(1);
            }

            psType.setString(1, "Rose");
            psType.executeUpdate();
            try (ResultSet rs = psType.getGeneratedKeys()) {
                assertTrue(rs.next());
                flowerTypeId = rs.getInt(1);
            }

            psFlower.setString(1, "Red Rose");
            psFlower.setDouble(2, 12.5);
            psFlower.setString(3, LocalDate.of(2025, 6, 10).toString());
            psFlower.setInt(4, flowerTypeId);
            psFlower.executeUpdate();
            try (ResultSet rs = psFlower.getGeneratedKeys()) {
                assertTrue(rs.next());
                flowerId = rs.getInt(1);
            }
        }
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testInsertAndFindByBouquetId() throws SQLException {
        FlowerInBouquet fib = new FlowerInBouquet();
        Flower flower = new Flower();
        flower.setId(flowerId);
        fib.setFlower(flower);

        Bouquet bouquet = new Bouquet();
        bouquet.setId(bouquetId);
        fib.setBouquet(bouquet);

        fib.setStemLength(25.5);
        fib.setQuantity(10);

        FlowerInBouquet inserted = dao.insert(fib);
        assertTrue(inserted.getId() > 0);

        List<FlowerInBouquet> list = dao.findByBouquetId(bouquetId);
        assertFalse(list.isEmpty());

        FlowerInBouquet found = list.get(0);
        assertEquals(inserted.getId(), found.getId());
        assertEquals(flowerId, found.getFlower().getId());
        assertEquals("Red Rose", found.getFlower().getName());
        assertEquals(12.5, found.getFlower().getPrice());
        assertEquals(LocalDate.of(2025, 6, 10), found.getFlower().getPickedDate());
        assertEquals(flowerTypeId, found.getFlower().getType().getId());
        assertEquals("Rose", found.getFlower().getType().getName());
        assertEquals(25.5, found.getStemLength());
        assertEquals(10, found.getQuantity());
        assertEquals(bouquetId, found.getBouquet().getId());
    }

    @Test
    void testDeleteByBouquetId() throws SQLException {
        FlowerInBouquet fib = new FlowerInBouquet();
        Flower flower = new Flower();
        flower.setId(flowerId);
        fib.setFlower(flower);

        Bouquet bouquet = new Bouquet();
        bouquet.setId(bouquetId);
        fib.setBouquet(bouquet);

        fib.setStemLength(30);
        fib.setQuantity(5);

        FlowerInBouquet inserted = dao.insert(fib);
        assertTrue(inserted.getId() > 0);

        // Переконуємось, що запис є
        List<FlowerInBouquet> beforeDelete = dao.findByBouquetId(bouquetId);
        assertTrue(beforeDelete.stream().anyMatch(f -> f.getId() == inserted.getId()));

        dao.deleteByBouquetId(bouquetId);

        List<FlowerInBouquet> afterDelete = dao.findByBouquetId(bouquetId);
        assertTrue(afterDelete.stream().noneMatch(f -> f.getId() == inserted.getId()));
    }
}
