package com.example.kursova_flowers.dao;

import com.example.kursova_flowers.model.Flower;
import com.example.kursova_flowers.model.FlowerType;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlowerDAOTest {

    private Connection connection;
    private FlowerDAO flowerDAO;
    private FlowerTypeDAO flowerTypeDAO;
    private FlowerType testType;

    @BeforeEach
    void setUp() throws Exception {
        // In-memory база
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        flowerDAO = new FlowerDAO(connection);
        flowerTypeDAO = new FlowerTypeDAO(connection);

        // Створення таблиць
        flowerTypeDAO.createTable();
        flowerDAO.createTable();

        // Додавання типу для тестів
        testType = new FlowerType(1, "Роза");
        flowerTypeDAO.insert(testType);
    }

    @AfterEach
    void tearDown() throws Exception {
        connection.close();
    }

    @Test
    void testInsertAndFindByType() throws Exception {
        Flower flower = new Flower();
        flower.setName("Троянда");
        flower.setPrice(15.0);
        flower.setPickedDate(LocalDate.now());
        flower.setType(testType);

        flowerDAO.insert(flower);

        List<Flower> found = flowerDAO.findByType(testType);
        assertEquals(1, found.size());
        assertEquals("Троянда", found.get(0).getName());
        assertEquals(15.0, found.get(0).getPrice());
        assertEquals(flower.getPickedDate(), found.get(0).getPickedDate());
    }

    @Test
    void testUpdate() throws Exception {
        Flower flower = new Flower();
        flower.setName("Півонія");
        flower.setPrice(12.0);
        flower.setPickedDate(LocalDate.now());
        flower.setType(testType);

        flowerDAO.insert(flower);

        // зміна даних
        flower.setName("Лілія");
        flower.setPrice(20.0);
        flowerDAO.update(flower);

        List<Flower> updatedList = flowerDAO.findByType(testType);
        assertEquals(1, updatedList.size());
        assertEquals("Лілія", updatedList.get(0).getName());
        assertEquals(20.0, updatedList.get(0).getPrice());
    }

    @Test
    void testDelete() throws Exception {
        Flower flower = new Flower();
        flower.setName("Гвоздика");
        flower.setPrice(9.5);
        flower.setPickedDate(LocalDate.now());
        flower.setType(testType);

        flowerDAO.insert(flower);
        flowerDAO.delete(flower);

        List<Flower> remaining = flowerDAO.findByType(testType);
        assertTrue(remaining.isEmpty());
    }
}
