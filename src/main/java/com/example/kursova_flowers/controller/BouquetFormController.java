package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.db.DBManager;
import com.example.kursova_flowers.model.Bouquet;
import com.example.kursova_flowers.util.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BouquetFormController {

    private static final Logger LOGGER = Logger.getLogger(BouquetFormController.class.getName());

    @FXML private VBox leftPanel;
    @FXML private Button backButton;
    @FXML private TextField nameField;

    @FXML private BorderPane rightPanel;

    java.sql.Connection connection;

    private Node flowersSectionNode;
    private FlowersSectionController flowersSectionController;

    private Node accessoriesSectionNode;
    private AccessoriesSectionController accessoriesSectionController;

    private Node receiptSectionNode;
    private ReceiptSectionController receiptSectionController;

    private Bouquet currentBouquet;

    @FXML
    public void initialize() {
       try {
          connection = DBManager.getConnection();
           LOGGER.info("Ініціалізація BouquetFormController...");

           loadFlowersPage();
           loadAccessoriesPage();
           loadSummaryPage();

           showPage(flowersSectionNode);

       } catch (IOException e) {
           e.printStackTrace();
           LOGGER.log(Level.SEVERE, "Помилка під час ініціалізації контролера букету", e);
       }
   }

    private void loadFlowersPage() throws IOException {
        LOGGER.info("Завантаження сторінки квітів...");
        flowersSectionController = SceneUtil.loadSection(
                Scenes.SECTIONFLOWER.getFxmlPath(),
                controller -> controller.setConnection(connection),
                node -> flowersSectionNode = node
        );
    }

    private void loadAccessoriesPage() throws IOException {
        LOGGER.info("Завантаження сторінки квітів...");
        accessoriesSectionController = SceneUtil.loadSection(
                Scenes.SECTIONACCESSORY.getFxmlPath(),
                controller -> controller.setConnection(connection),
                node -> accessoriesSectionNode = node
        );
    }

    /**
     * Завантажує FXML та контролер підсумкової сторінки (чек),
     * ініціалізує зв'язки між контролерами,
     * зв'язує поле введення назви букета.
     *
     * @throws IOException у разі помилки завантаження FXML
     */
    private void loadSummaryPage() throws IOException {
        LOGGER.info("Завантаження підсумкової сторінки...");
        receiptSectionController = SceneUtil.loadSection(
                Scenes.SECTIONRECEIPT.getFxmlPath(),
                controller -> {
                    controller.setConnection(connection);
                    controller.setControllers(flowersSectionController, accessoriesSectionController, this);
                    controller.bindBouquetName(nameField);
                    },
                node -> receiptSectionNode = node
        );
    }

    private void showPage(Node page) {
        rightPanel.setCenter(page);
    }

    @FXML
    private void showFlowersPage() {
        showPage(flowersSectionNode);
    }

    @FXML
    private void showAccessoriesPage() {
        showPage(accessoriesSectionNode);
    }

    @FXML
    private void showSummaryPage() {
        showPage(receiptSectionNode);
        receiptSectionController.refreshData();
    }

    @FXML
    private void goBack() {
        SceneUtil.openSceneFromButton(backButton, Scenes.MAIN);
    }

    public String getBouquetName() {
        return nameField.getText();
    }

    public Bouquet getCurrentBouquet() {
        return currentBouquet;
    }

    /**
     * Встановлює букет для редагування у формі.
     * Оновлює поле назви, заповнює квіти та аксесуари відповідних контролерів.
     * Оновлює дані у підсумковій секції.
     *
     * @param bouquet букет для редагування, або null для очищення форми
     */
    public void setBouquet(Bouquet bouquet) {
        this.currentBouquet = bouquet;
        if (bouquet != null) {
            LOGGER.info("Завантаження існуючого букету: " + bouquet.getName());
            nameField.setText(bouquet.getName());

            flowersSectionController.setFlowersInBouquet(FXCollections.observableArrayList(bouquet.getFlowers()));
            accessoriesSectionController.setAccessories(FXCollections.observableArrayList(bouquet.getAccessories()));

            if (receiptSectionController != null) {
                receiptSectionController.refreshData();
            }
        } else {
            LOGGER.warning("Передано порожнє значення букету в setBouquet.");
        }
    }

}
