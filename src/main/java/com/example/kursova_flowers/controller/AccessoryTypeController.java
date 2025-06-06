package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.dao.AccessoryTypeDAO;
import com.example.kursova_flowers.db.DBManager;
import com.example.kursova_flowers.model.AccessoryType;
import com.example.kursova_flowers.util.SceneUtil;
import com.example.kursova_flowers.util.Scenes;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

import java.sql.Connection;
import java.sql.SQLException;

public class AccessoryTypeController {
    @FXML
    private TableView<AccessoryType> accessoryTable;

    @FXML
    private TableColumn<AccessoryType, String> nameColumn;

    @FXML
    private TableColumn<AccessoryType, Double> priceColumn;

    @FXML
    private TableColumn<AccessoryType, Void> saveColumn;

    private final ObservableList<AccessoryType> accessories = FXCollections.observableArrayList();

    private AccessoryTypeDAO accessoryDAO;

    @FXML
    private Button backToMainButton;
    @FXML
    private void handleOpenScene(ActionEvent event) {
        SceneUtil.openSceneFromButton(backToMainButton, Scenes.MAIN);
    }

    public void initialize() {
        try {
            Connection connection = DBManager.getConnection();
            accessoryDAO = new AccessoryTypeDAO(connection);
            accessoryDAO.createTable();
            accessoryDAO.insertDefaultAccessoriesIfEmpty();
            loadAccessories();
        } catch (SQLException e) {
            showError("Помилка ініціалізації DAO", e.getMessage());
        }

        setupTable();
    }

    private void loadAccessories() {
        try {
            accessories.clear();
            accessories.addAll(accessoryDAO.findAll());
        } catch (SQLException e) {
            showError("Помилка завантаження аксесуарів", e.getMessage());
        }
    }

    private void setupTable() {
        accessoryTable.setItems(accessories);
        accessoryTable.setEditable(true);

        // Назва — тільки для читання
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameColumn.setEditable(false);

        // Ціна — редагована
        priceColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getBasePrice()).asObject());
        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        priceColumn.setOnEditCommit(event -> {
            AccessoryType accessoryType = event.getRowValue();
            accessoryType.setBasePrice(event.getNewValue());
            accessoryTable.refresh();
        });

        // Кнопка збереження
        saveColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("✔");
            {
                btn.setOnAction(e -> {
                    AccessoryType accessoryType = getTableView().getItems().get(getIndex());
                    try {
                        accessoryDAO.updateBasePrice(accessoryType.getId(), accessoryType.getBasePrice());
                        loadAccessories();
                    } catch (SQLException ex) {
                        showError("Помилка збереження", ex.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

