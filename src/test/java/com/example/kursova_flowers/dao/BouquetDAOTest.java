package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.model.Bouquet;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BouquetDAOTest {

    private static Connection connection;
    private static BouquetDAO bouquetDAO;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        bouquetDAO = new BouquetDAO(connection) {
            @Override
            public List<Bouquet> findAll() throws SQLException {
                List<Bouquet> bouquets = new ArrayList<>();
                String sql = "SELECT * FROM bouquet ORDER BY id DESC";
                try (PreparedStatement stmt = connection.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Bouquet bouquet = new Bouquet();
                        bouquet.setId(rs.getInt("id"));
                        bouquet.setName(rs.getString("name"));

                        // Замість реального DAO — просто пусті списки
                        bouquet.setFlowers(new ArrayList<>());
                        bouquet.setAccessories(new ArrayList<>());

                        bouquets.add(bouquet);
                    }
                }
                return bouquets;
            }
        };

        bouquetDAO.createTable();
    }




    @AfterAll
    static void closeDatabase() throws SQLException {
        connection.close();
    }

    @BeforeEach
    void clearTable() throws SQLException {
        connection.createStatement().execute("DELETE FROM bouquet");
    }

    @Test
    void testInsertAndFindAll() throws SQLException {
        Bouquet bouquet = new Bouquet();
        bouquet.setName("Summer Flowers");

        Bouquet inserted = bouquetDAO.insert(bouquet);
        assertNotNull(inserted.getId());
        assertEquals("Summer Flowers", inserted.getName());

        List<Bouquet> all = bouquetDAO.findAll();
        assertEquals(1, all.size());
        assertEquals("Summer Flowers", all.get(0).getName());
    }

    @Test
    void testUpdate() throws SQLException {
        Bouquet bouquet = new Bouquet();
        bouquet.setName("Initial Name");
        bouquet = bouquetDAO.insert(bouquet);

        bouquet.setName("Updated Name");
        bouquetDAO.update(bouquet);

        List<Bouquet> all = bouquetDAO.findAll();
        assertEquals(1, all.size());
        assertEquals("Updated Name", all.get(0).getName());
    }

    @Test
    void testDelete() throws SQLException {
        Bouquet bouquet = new Bouquet();
        bouquet.setName("To Delete");
        bouquet = bouquetDAO.insert(bouquet);

        bouquetDAO.delete(bouquet.getId());

        List<Bouquet> all = bouquetDAO.findAll();
        assertTrue(all.isEmpty());
    }

    @Test
    void testFindAllMultiple() throws SQLException {
        bouquetDAO.insert(new Bouquet(0, "A", List.of(), List.of()));
        bouquetDAO.insert(new Bouquet(0, "B", List.of(), List.of()));
        bouquetDAO.insert(new Bouquet(0, "C", List.of(), List.of()));

        List<Bouquet> all = bouquetDAO.findAll();
        assertEquals(3, all.size());
        assertEquals("C", all.get(0).getName()); // порядок: DESC
        assertEquals("B", all.get(1).getName());
        assertEquals("A", all.get(2).getName());
    }
}
