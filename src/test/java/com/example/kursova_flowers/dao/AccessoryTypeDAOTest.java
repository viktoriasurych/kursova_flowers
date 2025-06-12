package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.model.AccessoryType;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccessoryTypeDAOTest {
    private static Connection connection;
    private static AccessoryTypeDAO accessoryTypeDAO;

    @BeforeAll
    static void setup() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        accessoryTypeDAO = new AccessoryTypeDAO(connection);
        accessoryTypeDAO.createTable();
    }

    @AfterAll
    static void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testInsertDefaultAccessoriesIfEmpty() throws SQLException {
        accessoryTypeDAO.insertDefaultAccessoriesIfEmpty();
        List<AccessoryType> list = accessoryTypeDAO.findAll();
        assertEquals(4, list.size());
        assertEquals("листівка", list.get(0).getName());
    }

    @Test
    void testUpdateBasePrice() throws SQLException {
        accessoryTypeDAO.insertDefaultAccessoriesIfEmpty();
        List<AccessoryType> list = accessoryTypeDAO.findAll();
        AccessoryType type = list.get(0);

        accessoryTypeDAO.updateBasePrice(type.getId(), 12.5);
        List<AccessoryType> updated = accessoryTypeDAO.findAll();

        assertEquals(12.5, updated.get(0).getBasePrice(), 0.01);
    }

    @Test
    void testFindAllReturnsCorrectOrder() throws SQLException {
        List<AccessoryType> list = accessoryTypeDAO.findAll();
        for (int i = 1; i < list.size(); i++) {
            assertTrue(list.get(i - 1).getId() < list.get(i).getId());
        }
    }
}
