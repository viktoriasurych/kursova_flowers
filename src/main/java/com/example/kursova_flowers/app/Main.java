package com.example.kursova_flowers.app;

import com.example.kursova_flowers.db.DBManager;
import com.example.kursova_flowers.util.SceneUtil;
import javafx.application.Application;
import javafx.stage.Stage;
import com.example.kursova_flowers.util.Scenes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;


public class Main extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage stage) {
        URL resource = getClass().getResource("/com/example/kursova_flowers/img/back.png");
        System.out.println(resource);

        logger.info("Запуск додатку. Ініціалізація бази даних...");
        try {
            DBManager.init();
            logger.info("Ініціалізація бази даних успішна.");
        } catch (Exception e) {
            logger.error("Помилка при ініціалізації бази даних: {}", e.getMessage(), e);
        }

        SceneUtil.setScene(stage, Scenes.MAIN);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        try {
            DBManager.close();
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
