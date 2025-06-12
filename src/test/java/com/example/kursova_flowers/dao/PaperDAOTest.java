package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.dao.PaperDAO;
import com.example.kursova_flowers.model.Paper;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class PaperDAOTest {
    private static Connection connection;
    private static PaperDAO paperDAO;

    @BeforeAll
    static void setup() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        connection.createStatement().execute("CREATE TABLE accessory (id INTEGER PRIMARY KEY AUTOINCREMENT)");
        paperDAO = new PaperDAO(connection);
        paperDAO.createTable();
    }

    @Test
    void testInsertAndDeletePaper() throws SQLException {
        int accessoryId = insertDummyAccessory();
        Paper paper = new Paper();
        paper.setId(accessoryId);
        paper.setMaterial("Silk");

        Paper inserted = paperDAO.insert(paper);
        assertNotNull(inserted);
        assertEquals("Silk", inserted.getMaterial());

        paperDAO.delete(inserted.getId());
    }

    private int insertDummyAccessory() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO accessory DEFAULT VALUES");
            ResultSet rs = stmt.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : stmt.executeQuery("SELECT last_insert_rowid()").getInt(1);
        }
    }
}
