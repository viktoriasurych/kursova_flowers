package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.util.SceneUtil;
import com.example.kursova_flowers.util.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MainController {
    @FXML private Button createBtn;
    @FXML private Button bouquetsBtn;
    @FXML private Button flowersBtn;
    @FXML private Button accessorysBtn;
    @FXML private Button exitBtn;

    @FXML
    public void initialize() {

        createBtn.setOnAction(e -> openScene(Scenes.CREATE));
        bouquetsBtn.setOnAction(e -> openScene(Scenes.BOUQUET));
        flowersBtn.setOnAction(e -> openScene(Scenes.FLOWER));
        accessorysBtn.setOnAction(e -> openScene(Scenes.ACCESSORY));
        exitBtn.setOnAction(e -> System.exit(0));




    }

    private void openScene(Scenes scene) {
        Stage stage = (Stage) createBtn.getScene().getWindow();
        SceneUtil.setScene(stage, scene);
    }


}
