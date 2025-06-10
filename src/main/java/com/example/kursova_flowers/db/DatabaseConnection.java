package com.example.kursova_flowers.db;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Клас {@code DatabaseConnection} забезпечує підключення до бази даних SQLite.
 * Використовує вбудований файл бази даних, що зберігається в ресурсах проєкту.
 */
public class DatabaseConnection {

    private static final String DB_PATH = "src/main/resources/com/example/kursova_flowers/db/kursova_flowers.db";

    /**
     * Повертає об'єкт {@link Connection} до бази даних SQLite.
     * Завантажує драйвер, створює підключення та вмикає зовнішні ключі (foreign keys).
     *
     * @return {@link Connection} — підключення до бази даних.
     * @throws SQLException якщо сталася помилка під час підключення до бази даних.
     * @throws RuntimeException якщо драйвер SQLite не знайдено.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
        String absolutePath = new File(DB_PATH).getAbsolutePath();
        String url = "jdbc:sqlite:" + absolutePath;
        Connection connection = DriverManager.getConnection(url);

        connection.createStatement().execute("PRAGMA foreign_keys = ON");
        return connection;
    }

}
