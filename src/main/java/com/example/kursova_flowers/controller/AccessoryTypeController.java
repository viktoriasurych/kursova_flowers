package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.dao.AccessoryTypeDAO;
import com.example.kursova_flowers.db.DBManager;
import com.example.kursova_flowers.model.AccessoryType;
import com.example.kursova_flowers.util.SceneUtil;
import com.example.kursova_flowers.util.Scenes;
import com.example.kursova_flowers.util.ShowErrorUtil;
import com.example.kursova_flowers.util.TableColumnUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccessoryTypeController {
    private static final Logger LOGGER = Logger.getLogger(AccessoryTypeController.class.getName());
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
        LOGGER.info("Ініціалізація AccessoryTypeController...");
        try {
            Connection connection = DBManager.getConnection();
            accessoryDAO = new AccessoryTypeDAO(connection);
            accessoryDAO.createTable();
            accessoryDAO.insertDefaultAccessoriesIfEmpty();
            loadAccessories();
            LOGGER.info("Аксесуари успішно завантажено.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Помилка ініціалізації DAO", e);
            ShowErrorUtil.showError("Помилка ініціалізації DAO", e.getMessage());

        }

        setupTable();
    }

    private void loadAccessories() {
        LOGGER.info("Завантаження аксесуарів з бази даних...");
        try {
            accessories.clear();
            accessories.addAll(accessoryDAO.findAll());
            LOGGER.info("Аксесуари завантажено: " + accessories.size());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Помилка завантаження аксесуарів", e);
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
                LOGGER.info("Збереження аксесуара: ID=" + accessoryType.getId() + ", нова ціна=" + accessoryType.getBasePrice());
                accessoryDAO.updateBasePrice(accessoryType.getId(), accessoryType.getBasePrice());
                loadAccessories();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Помилка збереження аксесуара", ex);
                ShowErrorUtil.showError("Помилка збереження", ex.getMessage());
            }
        });
    }


}

