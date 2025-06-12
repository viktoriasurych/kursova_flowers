package com.example.kursova_flowers.db;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseConnectionTest {

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
    void testConnectionIsNotNull() {
        assertNotNull(connection, "Connection should not be null");
    }

    @Test
    void testConnectionIsValid() throws SQLException {
        assertTrue(connection.isValid(2), "Connection should be valid");
    }
}

