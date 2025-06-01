package com.example.kursova_flowers.db;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_PATH = "src/main/resources/com/example/kursova_flowers/db/kursova_flowers.db";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
        String absolutePath = new File(DB_PATH).getAbsolutePath();
        String url = "jdbc:sqlite:" + absolutePath;
        return DriverManager.getConnection(url);
    }

}
