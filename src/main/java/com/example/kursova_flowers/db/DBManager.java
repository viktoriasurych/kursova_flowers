package com.example.kursova_flowers.db;

import java.sql.Connection;
import java.sql.SQLException;

public class DBManager {
    private static Connection connection;

    // Ініціалізація з'єднання і створення таблиць
    public static void init() throws SQLException {
        connection = DatabaseConnection.getConnection();
        new DatabaseInitializer(connection).initialize();
    }

    // Отримати існуюче підключення
    public static Connection getConnection() {
        return connection;
    }

    // Закрити підключення (при завершенні програми)
    public static void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
