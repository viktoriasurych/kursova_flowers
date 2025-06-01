package com.example.kursova_flowers.db;

import com.example.kursova_flowers.dao.*;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseInitializer {
    private final Connection connection;

    public DatabaseInitializer(Connection connection) {
        this.connection = connection;
    }

    public void initialize() {
        try {
            new FlowerTypeDAO(connection).createTable();
            new FlowerDAO(connection).createTable();
            new BouquetDAO(connection).createTable();
            new FlowerInBouquetDAO(connection).createTable();
            new AccessoryTypeDAO(connection).createTable();
            new AccessoryDAO(connection).createTable();
            new CardDAO(connection).createTable();
            new RibbonDAO(connection).createTable();
            new BoxDAO(connection).createTable();
            new PaperDAO(connection).createTable();
        } catch (SQLException e) {
            throw new RuntimeException("Помилка при ініціалізації БД", e);
        }
    }
}
