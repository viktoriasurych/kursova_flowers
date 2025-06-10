package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.dao.FlowerDAO;
import com.example.kursova_flowers.dao.FlowerTypeDAO;
import com.example.kursova_flowers.db.DBManager;
import com.example.kursova_flowers.model.Flower;
import com.example.kursova_flowers.model.FlowerType;
import com.example.kursova_flowers.util.SceneUtil;
import com.example.kursova_flowers.util.Scenes;
import com.example.kursova_flowers.util.ShowErrorUtil;
import com.example.kursova_flowers.util.TableColumnUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class FlowerViewController {

    @FXML
    private ListView<FlowerType> flowerTypeList;

    @FXML
    private Button addButton;

    @FXML private Label selectedTypeLabel;
    @FXML private Label flowerCountLabel;
    @FXML private TableView<Flower> flowerTable;
    @FXML private TableColumn<Flower, String> nameColumn;
    @FXML private TableColumn<Flower, Double> priceColumn;
    @FXML private TableColumn<Flower, LocalDate> dateColumn;

    @FXML private TextField newTypeField;

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private DatePicker datePicker;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    @FXML
    private Button backToMainButton;

    @FXML
    private void handleOpenScene(ActionEvent event) {
        SceneUtil.openSceneFromButton(backToMainButton, Scenes.MAIN);
    }

    private final ObservableList<FlowerType> flowerTypes = FXCollections.observableArrayList();

    private FlowerTypeDAO flowerTypeDAO;
    private final ObservableList<Flower> flowers = FXCollections.observableArrayList();
    private FlowerDAO flowerDAO;
    private FlowerType selectedType;


    public void initialize() {
        initializeDAOs();
        initializeFlowerTypeList();
        initializeEventHandlers();
        setupEditableColumns();
        flowerTable.setItems(flowers);

    }

    private void initializeDAOs() {
        try {
            Connection connection = DBManager.getConnection(); // Отримуємо єдине підключення
            flowerTypeDAO = new FlowerTypeDAO(connection);
            flowerDAO = new FlowerDAO(connection);
            loadFlowerTypes();
        } catch (Exception e) {
            ShowErrorUtil.showError("Помилка ініціалізації DAO", e.getMessage());
        }
    }

    private void initializeFlowerTypeList() {
        flowerTypeList.setItems(flowerTypes);
        flowerTypeList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(FlowerType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
    }

    private void initializeEventHandlers() {
        addButton.setOnAction(e -> onAddFlowerType());

        flowerTypeList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedType = newVal;
                selectedTypeLabel.setText(newVal.getName());
                loadFlowersByType(newVal);
            }
        });

        flowerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillForm(newSelection);
            } else {
                clearForm();
            }
        });

        updateButton.setOnAction(e -> onUpdateButtonClicked());
        deleteButton.setOnAction(e -> onDeleteButtonClicked());
    }

    private void onUpdateButtonClicked() {
        Flower selected = flowerTable.getSelectionModel().getSelectedItem();

        if (selected != null) {
            updateExistingFlower(selected);
        } else {
            addNewFlower();
        }

        clearForm();
    }

    private void updateExistingFlower(Flower selected) {
        updateFlowerFromForm(selected);
        try {
            if (selected.getId() == 0) {
                flowerDAO.insert(selected);
            } else {
                flowerDAO.update(selected);
            }
            loadFlowersByType(selected.getType());
            flowerTable.getSelectionModel().select(selected);
        } catch (SQLException ex) {
            ShowErrorUtil.showError("Помилка збереження", ex.getMessage());
        }
    }

    private void addNewFlower() {
        if (selectedType == null) {
            ShowErrorUtil.showError("Не обрано тип квітки", "Будь ласка, виберіть тип квітки зліва.");
            return;
        }

        Flower newFlower = new Flower();
        newFlower.setType(selectedType);
        updateFlowerFromForm(newFlower);

        try {
            flowerDAO.insert(newFlower);
            loadFlowersByType(selectedType);
            flowerTable.getSelectionModel().selectLast();
        } catch (SQLException ex) {
            ShowErrorUtil.showError("Помилка додавання квітки", ex.getMessage());
        }
    }

    private void onDeleteButtonClicked() {
        Flower selected = flowerTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getId() == 0) {
                flowers.remove(selected);
                clearForm();
            } else {
                try {
                    flowerDAO.delete(selected);
                    loadFlowersByType(selected.getType());
                    clearForm();
                } catch (SQLException ex) {
                    ShowErrorUtil.showError("Помилка видалення", ex.getMessage());
                }
            }
        }
    }

    private void fillForm(Flower flower) {
        nameField.setText(flower.getName());
        priceField.setText(String.valueOf(flower.getPrice()));
        datePicker.setValue(flower.getPickedDate());
    }

    private void updateFlowerFromForm(Flower flower) {
        flower.setName(nameField.getText());
        try {
            flower.setPrice(Double.parseDouble(priceField.getText()));
        } catch (NumberFormatException e) {
            flower.setPrice(0.0);
        }

        flower.setPickedDate(datePicker.getValue());
    }

    private void loadFlowerTypes() {
        flowerTypes.clear();
        try {
            flowerTypes.addAll(flowerTypeDAO.findAll());

            if (!flowerTypes.isEmpty()) {
                Platform.runLater(() -> flowerTypeList.getSelectionModel().selectFirst());
            }

        } catch (SQLException e) {
            ShowErrorUtil.showError("Помилка завантаження типів квітів", e.getMessage());
        }
    }

    private void onAddFlowerType() {
        String name = newTypeField.getText().trim();
        if (name.isEmpty()) {
            ShowErrorUtil.showError("Помилка", "Назва не може бути пустою");
            return;
        }

        FlowerType newType = new FlowerType(0, name);
        try {
            flowerTypeDAO.insert(newType);
            loadFlowerTypes();
            newTypeField.clear(); // очистити після додавання
        } catch (SQLException ex) {
            ShowErrorUtil.showError("Помилка додавання", ex.getMessage());
        }
    }

    private void loadFlowersByType(FlowerType type) {
        try {
            System.out.println("Selected FlowerType id = " + type.getId());

            flowers.setAll(flowerDAO.findByType(type));
            flowerCountLabel.setText("(" + flowers.size() + " квіток)");
        } catch (SQLException e) {
            ShowErrorUtil.showError("Помилка завантаження квітів", e.getMessage());
        }
    }

    private void setupEditableColumns() {
        flowerTable.setEditable(true);

        flowerTable.getSelectionModel().selectedIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            if (oldIndex != null && oldIndex.intValue() >= 0) {
                TablePosition<Flower, ?> editingCell = flowerTable.getEditingCell();
                if (editingCell != null) {
                    flowerTable.edit(-1, null);
                }
            }
        });

        TableColumnUtil.makeEditableStringColumn(nameColumn,
                Flower::getName,
                (flower, newName) -> {
                    flower.setName(newName);
                    flowerTable.edit(-1, null);
                }
        );

        TableColumnUtil.makeEditableDoubleColumn(priceColumn,
                Flower::getPrice,
                (flower, newPrice) -> {
                    flower.setPrice(newPrice);
                    flowerTable.edit(-1, null);
                }
        );


        TableColumnUtil.makeEditableDateColumn(dateColumn,
                Flower::getPickedDate,
                (flower, newDate) -> {
                    flower.setPickedDate(newDate);
                    flowerTable.edit(-1, null);
                }
        );
    }

    private void clearForm() {
        nameField.clear();
        priceField.clear();
        datePicker.setValue(null);
    }
}
