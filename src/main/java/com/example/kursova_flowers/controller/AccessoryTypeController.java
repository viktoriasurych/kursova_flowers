package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.dao.AccessoryTypeDAO;
import com.example.kursova_flowers.db.DBManager;
import com.example.kursova_flowers.model.AccessoryType;
import com.example.kursova_flowers.util.SceneUtil;
import com.example.kursova_flowers.util.Scenes;
import com.example.kursova_flowers.util.ShowErrorUtil;
import com.example.kursova_flowers.util.TableColumnUtil;
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
            ShowErrorUtil.showError("Помилка ініціалізації DAO", e.getMessage());

        }

        setupTable();
    }

    private void loadAccessories() {
        try {
            accessories.clear();
            accessories.addAll(accessoryDAO.findAll());
        } catch (SQLException e) {
            ShowErrorUtil.showError("Помилка завантаження аксесуарів", e.getMessage());
        }
    }

    private void setupTable() {
        accessoryTable.setItems(accessories);
        accessoryTable.setEditable(true);

        // Назва — тільки для читання
        TableColumnUtil.makeReadOnlyStringColumn(nameColumn, AccessoryType::getName);

        // Ціна — редагована колонка
        TableColumnUtil.makeEditableDoubleColumn(priceColumn,
                AccessoryType::getBasePrice,
                AccessoryType::setBasePrice);

        // Кнопка збереження
        TableColumnUtil.makeSaveButtonColumn(saveColumn, (accessoryType, index) -> {
            try {
                accessoryDAO.updateBasePrice(accessoryType.getId(), accessoryType.getBasePrice());
                loadAccessories();
            } catch (SQLException ex) {
                ShowErrorUtil.showError("Помилка збереження", ex.getMessage());
            }
        });
    }


}

