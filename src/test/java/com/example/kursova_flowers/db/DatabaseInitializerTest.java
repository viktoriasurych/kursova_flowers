package com.example.kursova_flowers.db;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseInitializerTest {

    private static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException {
        connection = DatabaseConnection.getConnection();
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void testInitializeCreatesTables() {
        DatabaseInitializer initializer = new DatabaseInitializer(connection);

        assertDoesNotThrow(initializer::initialize, "Initialization should not throw exception");

        // Тут можна додатково перевірити, що таблиці існують, наприклад так:
        // але це більш глибока перевірка, наприклад:
        try (var stmt = connection.createStatement()) {
            var rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");
            boolean foundAnyTable = false;
            while (rs.next()) {
                foundAnyTable = true;
                System.out.println("Table found: " + rs.getString("name"));
            }
            assertTrue(foundAnyTable, "Tables should be created");
        } catch (SQLException e) {
            fail("SQLException during checking tables: " + e.getMessage());
        }
    }
}

