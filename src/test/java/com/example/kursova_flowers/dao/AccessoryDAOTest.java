package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.dao.AccessoryDAO;
import com.example.kursova_flowers.model.*;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccessoryDAOTest {

    private static Connection connection;
    private static AccessoryDAO accessoryDAO;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        // Створюємо потрібні допоміжні таблиці для FK
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE bouquet (
                    id INTEGER PRIMARY KEY AUTOINCREMENT
                )
            """);
            stmt.execute("""
                CREATE TABLE accessory_type (
                    id INTEGER PRIMARY KEY,
                    name TEXT,
                    base_price REAL
                )
            """);
            // Таблиці для box, ribbon, paper, card
            stmt.execute("CREATE TABLE box (accessory_id INTEGER PRIMARY KEY, style TEXT)");
            stmt.execute("CREATE TABLE ribbon (accessory_id INTEGER PRIMARY KEY, width INTEGER)");
            stmt.execute("CREATE TABLE paper (accessory_id INTEGER PRIMARY KEY, material TEXT)");
            stmt.execute("CREATE TABLE card (accessory_id INTEGER PRIMARY KEY, card_text TEXT)");
        }

        accessoryDAO = new AccessoryDAO(connection);
        accessoryDAO.createTable();

        // Вставляємо дані в bouquet і accessory_type для тестів
        try (PreparedStatement psBouquet = connection.prepareStatement("INSERT INTO bouquet DEFAULT VALUES", Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psType = connection.prepareStatement("INSERT INTO accessory_type (id, name, base_price) VALUES (?, ?, ?)")) {
            // bouquet
            psBouquet.executeUpdate();
            try (ResultSet rs = psBouquet.getGeneratedKeys()) {
                assertTrue(rs.next());
                bouquetId = rs.getInt(1);
            }
            // accessory_type - вставляємо типи 1-4
            psType.setInt(1, 1);
            psType.setString(2, "GreetingCard");
            psType.setDouble(3, 10.0);
            psType.executeUpdate();

            psType.setInt(1, 2);
            psType.setString(2, "Box");
            psType.setDouble(3, 20.0);
            psType.executeUpdate();

            psType.setInt(1, 3);
            psType.setString(2, "Ribbon");
            psType.setDouble(3, 5.0);
            psType.executeUpdate();

            psType.setInt(1, 4);
            psType.setString(2, "Paper");
            psType.setDouble(3, 3.0);
            psType.executeUpdate();
        }
    }

    private static int bouquetId;

    @AfterAll
    static void closeConnection() throws SQLException {
        connection.close();
    }

    @Test
    void testInsertAndFindAccessory() throws SQLException {
        // Створюємо GreetingCard аксесуар
        GreetingCard card = new GreetingCard();
        card.setBouquet(new Bouquet());
        card.getBouquet().setId(bouquetId);
        card.setType(new AccessoryType());
        card.getType().setId(1);  // GreetingCard
        card.setColor("Red");
        card.setNote("Test note");
        card.setText("Happy Birthday");

        Accessory inserted = accessoryDAO.insert(card);
        assertTrue(inserted.getId() > 0);

        // Вставляємо в card table
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO card (accessory_id, card_text) VALUES (?, ?)")) {
            ps.setInt(1, inserted.getId());
            ps.setString(2, card.getText());
            ps.executeUpdate();
        }

        // Знаходимо аксесуари за bouquetId
        List<Accessory> accessories = accessoryDAO.findByBouquetId(bouquetId);
        assertFalse(accessories.isEmpty());

        Accessory found = accessories.get(0);
        assertEquals(inserted.getId(), found.getId());
        assertTrue(found instanceof GreetingCard);
        assertEquals("Red", found.getColor());
        assertEquals("Test note", found.getNote());
        assertEquals("Happy Birthday", ((GreetingCard) found).getText());
        assertEquals("GreetingCard", found.getType().getName());
    }

    @Test
    void testDeleteByBouquetId() throws SQLException {
        // Вставляємо аксесуар для видалення
        Accessory accessory = new Accessory();
        accessory.setBouquet(new Bouquet());
        accessory.getBouquet().setId(bouquetId);
        accessory.setType(new AccessoryType());
        accessory.getType().setId(2);  // Box
        accessory.setColor("Blue");
        accessory.setNote("ToDelete");

        Accessory inserted = accessoryDAO.insert(accessory);

        // Insert into box table
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO box (accessory_id, style) VALUES (?, ?)")) {
            ps.setInt(1, inserted.getId());
            ps.setString(2, "Classic");
            ps.executeUpdate();
        }

        // Переконуємось, що аксесуар є
        List<Accessory> accessoriesBefore = accessoryDAO.findByBouquetId(bouquetId);
        assertTrue(accessoriesBefore.stream().anyMatch(a -> a.getId() == inserted.getId()));

        // Видаляємо
        accessoryDAO.deleteByBouquetId(bouquetId);

        // Перевіряємо, що видалено
        List<Accessory> accessoriesAfter = accessoryDAO.findByBouquetId(bouquetId);
        assertTrue(accessoriesAfter.stream().noneMatch(a -> a.getId() == inserted.getId()));
    }
}
