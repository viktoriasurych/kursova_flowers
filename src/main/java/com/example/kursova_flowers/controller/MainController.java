package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.util.SceneUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController {
    @FXML private Button createBtn;
    @FXML private Button bouquetsBtn;
    @FXML private Button flowersBtn;
    @FXML private Button exitBtn;

    @FXML
    public void initialize() {
        createBtn.setOnAction(e -> openScene("create-view.fxml", "Створити"));
        bouquetsBtn.setOnAction(e -> openScene("bouquet-view.fxml", "Букети"));
        flowersBtn.setOnAction(e -> openScene("flower-view.fxml", "Квіти"));
        exitBtn.setOnAction(e -> System.exit(0));
    }

    private void openScene(String fxml, String title) {
        Stage stage = (Stage) createBtn.getScene().getWindow();
        SceneUtil.setScene(stage, "/com/example/kursova_flowers/app/" + fxml, title);
    }
}
