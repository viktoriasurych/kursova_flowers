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
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class BouquetsListController {
    @FXML
    private FlowPane bouquetsContainer;

    @FXML
    public void initialize() throws SQLException, IOException {
        loadBouquets();
    }

    private void loadBouquets() throws SQLException, IOException {
        BouquetDAO bdao = new BouquetDAO(DBManager.getConnection());
        List<Bouquet> bouquets = bdao.findAll();

        bouquetsContainer.getChildren().clear();
        for (Bouquet bouquet : bouquets) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/kursova_flowers/app/bouquet-card.fxml"));
            Parent card = loader.load();
            BouquetCardController controller = loader.getController();

            double totalPrice = new BouquetCalculatorService().calculateTotalPrice(bouquet);

            controller.setData(bouquet, totalPrice,
                    () -> openEditBouquetForm(bouquet), // onEdit
                    () -> deleteBouquet(bouquet)        // onDelete
            );

            bouquetsContainer.getChildren().add(card);
        }
    }

    private void openEditBouquetForm(Bouquet bouquet) {
        try {
            Stage stage = (Stage) bouquetsContainer.getScene().getWindow();
            // або інший спосіб отримати Stage
            FXMLLoader loader = SceneUtil.setScene(stage, Scenes.CREATE);
            if (loader != null) {
                BouquetFormController controller = loader.getController();
                controller.setBouquet(bouquet);
            }

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
       // SceneUtil.openScene((Stage) bouquetsContainer.getScene().getWindow(),
         //       getClass().getResource("/com/example/kursova_flowers/app/home-view.fxml"));
    }
}
