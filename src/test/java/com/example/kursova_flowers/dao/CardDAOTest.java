package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.dao.CardDAO;
import com.example.kursova_flowers.model.GreetingCard;
import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class CardDAOTest {
    private static Connection connection;
    private static CardDAO cardDAO;

    @BeforeAll
    static void setup() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        connection.createStatement().execute("CREATE TABLE accessory (id INTEGER PRIMARY KEY AUTOINCREMENT)");
        cardDAO = new CardDAO(connection);
        cardDAO.createTable();
    }

    @Test
    void testInsertAndDeleteCard() throws SQLException {
        int accessoryId = insertDummyAccessory();
        GreetingCard card = new GreetingCard();
        card.setId(accessoryId);
        card.setText("Happy Birthday!");

        GreetingCard inserted = cardDAO.insert(card);
        assertNotNull(inserted);
        assertEquals("Happy Birthday!", inserted.getText());

        cardDAO.delete(inserted.getId());
    }

    private int insertDummyAccessory() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO accessory DEFAULT VALUES");
            ResultSet rs = stmt.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : stmt.executeQuery("SELECT last_insert_rowid()").getInt(1);
        }
    }
}
