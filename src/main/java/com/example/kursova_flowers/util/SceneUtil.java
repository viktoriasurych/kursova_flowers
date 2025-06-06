package com.example.kursova_flowers.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import javafx.scene.control.Button;


public class SceneUtil {
    private static final double WIDTH = 1000;
    private static final double HEIGHT = 600;
    private static final String STYLESHEET = "/com/example/kursova_flowers/styles/style.css";

    public static void setScene(Stage stage, Scenes sceneEnum) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneUtil.class.getResource(sceneEnum.getFxmlPath()));
            Parent root = loader.load();

            Scene scene = new Scene(root, WIDTH, HEIGHT);
            scene.getStylesheets().add(SceneUtil.class.getResource(STYLESHEET).toExternalForm());

            stage.setTitle(sceneEnum.getTitle());
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void openSceneFromButton(Button button, Scenes scene) {
        Stage stage = (Stage) button.getScene().getWindow();
        setScene(stage, scene);
    }

}

