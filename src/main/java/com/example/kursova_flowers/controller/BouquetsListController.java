package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.dao.BouquetDAO;
import com.example.kursova_flowers.db.DBManager;
import com.example.kursova_flowers.model.Bouquet;
import com.example.kursova_flowers.util.SceneUtil;
import com.example.kursova_flowers.service.BouquetCalculatorService;
import com.example.kursova_flowers.util.Scenes;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class BouquetsListController {
    @FXML
    private FlowPane bouquetsContainer;
    @FXML
    private Button backButton;

    @FXML
    public void initialize() throws SQLException, IOException {
        loadBouquets();
    }

    private void loadBouquets() throws SQLException, IOException {
        BouquetDAO bdao = new BouquetDAO(DBManager.getConnection());
        List<Bouquet> bouquets = bdao.findAll();

        bouquetsContainer.getChildren().clear();

        for (Bouquet bouquet : bouquets) {
            // Завантажуємо fxml + контролер + додаємо до контейнера через утиліту
            BouquetCardController controller = SceneUtil.loadSection( Scenes.CARDBOUQUET.getFxmlPath(),
                    ctrl -> {
                        double totalPrice = new BouquetCalculatorService().calculateTotalPrice(bouquet);
                        ctrl.setData(bouquet, totalPrice,
                                () -> openEditBouquetForm(bouquet), // onEdit
                                () -> deleteBouquet(bouquet)        // onDelete
                        );
                    },
                    node -> bouquetsContainer.getChildren().add(node)
            );
        }
    }

    private void openEditBouquetForm(Bouquet bouquet) {
        try {
            Stage stage = (Stage) bouquetsContainer.getScene().getWindow();
            SceneUtil.setSceneWithController(
                    stage,
                    Scenes.CREATE,
                    (BouquetFormController controller) -> {
                        controller.setBouquet(bouquet);
                    }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteBouquet(Bouquet bouquet) {
        try {
            new BouquetDAO(DBManager.getConnection()).delete(bouquet.getId());
            loadBouquets(); // оновити список після видалення
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBackToHome() throws IOException {
        SceneUtil.openSceneFromButton(backButton, Scenes.MAIN);
    }
}
