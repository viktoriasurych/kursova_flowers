package com.example.kursova_flowers.app;

import com.example.kursova_flowers.db.DBManager;
import com.example.kursova_flowers.util.SceneUtil;
import javafx.application.Application;
import javafx.stage.Stage;
import com.example.kursova_flowers.util.Scenes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage stage) {
        logger.info("Запуск додатку. Ініціалізація бази даних...");

        try {
            DBManager.init();
            logger.info("Ініціалізація бази даних успішна.");
        } catch (Exception e) {
            logger.error("Помилка при ініціалізації бази даних: {}", e.getMessage(), e);
        }
        logger.info("Встановлення сцени на MAIN");
        SceneUtil.setScene(stage, Scenes.MAIN);
    }

    @Override
    public void stop() throws Exception {
        logger.info("Зупинка додатку. Закриття підключення до бази даних...");
        super.stop();
        try {
            DBManager.close();
            logger.info("Підключення до бази даних закрито успішно.");
        } catch (Exception e) {
            logger.error("Помилка при закритті підключення до бази даних: {}", e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        logger.info("Запуск методу main.");
        launch(args);
    }
}
