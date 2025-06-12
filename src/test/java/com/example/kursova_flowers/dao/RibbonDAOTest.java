package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.dao.RibbonDAO;
import com.example.kursova_flowers.model.Ribbon;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class RibbonDAOTest {
    private static Connection connection;
    private static RibbonDAO ribbonDAO;

    @BeforeAll
    static void setup() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        connection.createStatement().execute("CREATE TABLE accessory (id INTEGER PRIMARY KEY AUTOINCREMENT)");
        ribbonDAO = new RibbonDAO(connection);
        ribbonDAO.createTable();
    }

    @Test
    void testInsertAndDeleteRibbon() throws SQLException {
        int accessoryId = insertDummyAccessory();
        Ribbon ribbon = new Ribbon();
        ribbon.setId(accessoryId);
        ribbon.setWidth(2.5);

        Ribbon inserted = ribbonDAO.insert(ribbon);
        assertNotNull(inserted);
        assertEquals(2.5, inserted.getWidth());

        ribbonDAO.delete(inserted.getId());
    }

    private int insertDummyAccessory() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO accessory DEFAULT VALUES");
            ResultSet rs = stmt.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : stmt.executeQuery("SELECT last_insert_rowid()").getInt(1);
        }
    }
}
