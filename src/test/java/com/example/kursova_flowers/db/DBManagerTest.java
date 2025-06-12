package com.example.kursova_flowers.db;

import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DBManagerTest {

    @Test
    void testInitAndClose() {
        assertDoesNotThrow(() -> {
            DBManager.init();
            Connection conn = DBManager.getConnection();
            assertNotNull(conn, "DBManager.getConnection() should not return null");
            assertFalse(conn.isClosed(), "Connection should be open after init");
            DBManager.close();
            assertTrue(conn.isClosed(), "Connection should be closed after DBManager.close()");
        });
    }
}
