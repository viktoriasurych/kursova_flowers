package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainController {

    @FXML private Button createBtn;
    @FXML private Button bouquetsBtn;
    @FXML private Button flowersBtn;
    @FXML private Button accessorysBtn;
    @FXML private Button exitBtn;


    @FXML
    public void initialize() {
        createBtn.setOnAction(e -> SceneUtil.openSceneFromButton(createBtn, Scenes.CREATE));
        bouquetsBtn.setOnAction(e -> SceneUtil.openSceneFromButton(bouquetsBtn, Scenes.BOUQUET));
        flowersBtn.setOnAction(e -> SceneUtil.openSceneFromButton(flowersBtn, Scenes.FLOWER));
        accessorysBtn.setOnAction(e -> SceneUtil.openSceneFromButton(accessorysBtn, Scenes.ACCESSORY));
        exitBtn.setOnAction(e -> System.exit(0));
    }

}
