package com.example.kursova_flowers.app;

import com.example.kursova_flowers.db.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Встановити з'єднання з базою
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Ініціалізувати таблиці при запуску
            DatabaseInitializer initializer = new DatabaseInitializer(connection);
            initializer.initialize();
        } catch (SQLException e) {
            e.printStackTrace();
            // Тут можна додати логіку, якщо ініціалізація не пройшла
        }

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // створюється DAO


        // можна одразу додавати або читати

        launch();

    }
}