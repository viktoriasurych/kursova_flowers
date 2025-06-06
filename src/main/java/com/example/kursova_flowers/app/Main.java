package com.example.kursova_flowers.app;

import com.example.kursova_flowers.db.DBManager;
import com.example.kursova_flowers.util.SceneUtil;
import javafx.application.Application;
import javafx.stage.Stage;
import com.example.kursova_flowers.util.Scenes;


public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            DBManager.init();  // Відкриваємо БД один раз і ініціалізуємо
        } catch (Exception e) {
            e.printStackTrace();
            // Тут можна додати повідомлення для користувача або логування
        }

        SceneUtil.setScene(stage, Scenes.MAIN);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DBManager.close(); // Закриваємо підключення при виході з програми
    }

    public static void main(String[] args) {
        launch(args);
    }
}
