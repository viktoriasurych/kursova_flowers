package com.example.kursova_flowers.db;

import java.sql.Connection;
import java.sql.SQLException;

public class DBManager {
    private static Connection connection;

    /**
     * Ініціалізує підключення до бази даних та створює всі необхідні таблиці.
     *
     * @throws SQLException якщо виникає помилка при встановленні з'єднання
     */
    public static void init() throws SQLException {
        connection = DatabaseConnection.getConnection();
        new DatabaseInitializer(connection).initialize();
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
