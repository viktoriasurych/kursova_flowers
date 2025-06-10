package com.example.kursova_flowers.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.function.Consumer;

import javafx.scene.control.Button;


public class SceneUtil {
    private static final double WIDTH = 1000;
    private static final double HEIGHT = 600;
    private static final String STYLESHEET = "/com/example/kursova_flowers/styles/style.css";

    /**
     * Завантажує FXML-файл та встановлює сцену у вікно.
     *
     * @param stage     поточне вікно JavaFX
     * @param sceneEnum перелічення {@link Scenes}, що містить шлях до FXML та заголовок
     * @return {@link FXMLLoader} для доступу до контролера сцени
     */
    public static FXMLLoader setScene(Stage stage, Scenes sceneEnum) {
        FXMLLoader loader = null;
        try {
            loader = new FXMLLoader(SceneUtil.class.getResource(sceneEnum.getFxmlPath()));
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
        return loader;
    }

    /**
     * Завантажує секцію з FXML і передає контролер і вузол (Node) у відповідні споживачі.
     *
     * @param fxmlPath           шлях до FXML-файлу
     * @param controllerConsumer обробник контролера (може бути null)
     * @param nodeConsumer       обробник вузла сцени (може бути null)
     * @param <T>                тип контролера
     * @return екземпляр контролера
     * @throws IOException якщо FXML-файл не знайдено або не може бути прочитаний
     */
    public static <T> T loadSection(
            String fxmlPath,
            Consumer<T> controllerConsumer,
            Consumer<Parent> nodeConsumer
    ) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneUtil.class.getResource(fxmlPath));
        Parent node = loader.load();
        T controller = loader.getController();

        if (controllerConsumer != null) {
            controllerConsumer.accept(controller);
        }

        if (nodeConsumer != null) {
            nodeConsumer.accept(node);
        }

        return controller;
    }

    /**
     * Встановлює сцену та дозволяє працювати з контролером через переданий {@link Consumer}.
     *
     * @param stage              вікно JavaFX
     * @param sceneEnum          сцена з enum
     * @param controllerConsumer обробник контролера
     * @param <T>                тип контролера
     */
    public static <T> void setSceneWithController(Stage stage, Scenes sceneEnum, Consumer<T> controllerConsumer) {
        FXMLLoader loader = setScene(stage, sceneEnum);
        if (loader != null && controllerConsumer != null) {
            T controller = loader.getController();
            controllerConsumer.accept(controller);
        }
    }

    /**
     * Відкриває нову сцену при натисканні на кнопку.
     *
     * @param button кнопка, з якої було ініційовано перехід
     * @param scene  сцена, яка має бути відкритою
     */
    public static void openSceneFromButton(Button button, Scenes scene) {
        Stage stage = (Stage) button.getScene().getWindow();
        setScene(stage, scene);
    }


}

