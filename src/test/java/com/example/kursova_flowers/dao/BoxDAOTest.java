package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.dao.BoxDAO;
import com.example.kursova_flowers.model.Box;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class BoxDAOTest {
    private static Connection connection;
    private static BoxDAO boxDAO;

    @BeforeAll
    static void setup() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        connection.createStatement().execute("CREATE TABLE accessory (id INTEGER PRIMARY KEY AUTOINCREMENT)");
        boxDAO = new BoxDAO(connection);
        boxDAO.createTable();
    }

    @Test
    void testInsertAndDeleteBox() throws SQLException {
        int accessoryId = insertDummyAccessory();
        Box box = new Box();
        box.setId(accessoryId);
        box.setBoxType("Classic");

        Box inserted = boxDAO.insert(box);
        assertNotNull(inserted);
        assertEquals("Classic", inserted.getBoxType());

        boxDAO.delete(inserted.getId());
    }

    private int insertDummyAccessory() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO accessory DEFAULT VALUES");
            ResultSet rs = stmt.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : stmt.executeQuery("SELECT last_insert_rowid()").getInt(1);
        }
    }
}
