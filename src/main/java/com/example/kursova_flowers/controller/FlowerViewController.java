package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.dao.FlowerDAO;
import com.example.kursova_flowers.dao.FlowerTypeDAO;
import com.example.kursova_flowers.db.DBManager;
import com.example.kursova_flowers.model.Flower;
import com.example.kursova_flowers.model.FlowerType;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

public class FlowerViewController {

    @FXML
    private ListView<FlowerType> flowerTypeList;

    @FXML
    private Button addButton;

    @FXML
    private Pane detailPane;
    @FXML private Label selectedTypeLabel;
    @FXML private Label flowerCountLabel;
    @FXML private Button addFlowerButton;
    @FXML private TableView<Flower> flowerTable;
    @FXML private TableColumn<Flower, String> nameColumn;
    @FXML private TableColumn<Flower, Double> priceColumn;
    @FXML private TableColumn<Flower, Integer> quantityColumn;
    @FXML private TableColumn<Flower, LocalDate> dateColumn;
    @FXML private TableColumn<Flower, Void> saveColumn;
    @FXML private TableColumn<Flower, Void> deleteColumn;


    private final ObservableList<FlowerType> flowerTypes = FXCollections.observableArrayList();

    private FlowerTypeDAO flowerTypeDAO;
    private final ObservableList<Flower> flowers = FXCollections.observableArrayList();
    private FlowerDAO flowerDAO;
    private FlowerType selectedType;


    public void initialize() {
        try {
            Connection connection = DBManager.getConnection(); // Отримуємо єдине підключення
            flowerTypeDAO = new FlowerTypeDAO(connection);
            flowerDAO = new FlowerDAO(connection);
            loadFlowerTypes();
        } catch (Exception e) {
            showError("Помилка ініціалізації DAO", e.getMessage());
        }

        flowerTypeList.setItems(flowerTypes);
        flowerTypeList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(FlowerType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });

        addButton.setOnAction(e -> onAddFlowerType());

        flowerTypeList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedType = newVal;
                selectedTypeLabel.setText(newVal.getName());
                loadFlowersByType(newVal);
            }
        });

        addFlowerButton.setOnAction(e -> onAddNewFlowerRow());

        setupEditableColumns();
        setupActionColumns(); // для ✔ і ✖
        flowerTable.setItems(flowers);
    }

    private void loadFlowerTypes() {
        flowerTypes.clear();
        try {
            flowerTypes.addAll(flowerTypeDAO.findAll());
        } catch (SQLException e) {
            showError("Помилка завантаження типів квітів", e.getMessage());
        }
    }

    private void onAddFlowerType() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Додати тип квітки");
        dialog.setHeaderText(null);
        dialog.setContentText("Введіть назву нового типу:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (name.trim().isEmpty()) {
                showError("Помилка", "Назва не може бути пустою");
                return;
            }
            FlowerType newType = new FlowerType(0, name.trim());
            try {
                flowerTypeDAO.insert(newType);
                loadFlowerTypes();  // оновити список після додавання
            } catch (SQLException ex) {
                showError("Помилка додавання", ex.getMessage());
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

    private final ObservableList<Flower> currentFlowers = FXCollections.observableArrayList();




    private void loadFlowersByType(FlowerType type) {
        try {
            System.out.println("Selected FlowerType id = " + type.getId());

            flowers.setAll(flowerDAO.findByType(type));
            flowerCountLabel.setText("(" + flowers.size() + " квіток)");
        } catch (SQLException e) {
            showError("Помилка завантаження квітів", e.getMessage());
        }
    }
    private void onAddNewFlowerRow() {
        Flower newFlower = new Flower();
        newFlower.setType(selectedType);
        newFlower.setName("");
        newFlower.setPrice(0.0);
        newFlower.setTotalQuantity(1);
        newFlower.setPickedDate(LocalDate.now());

        flowers.add(newFlower);
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

        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(e -> {
            Flower flower = e.getRowValue();
            flower.setName(e.getNewValue());
            flowerTable.edit(-1, null); // завершуємо редагування
        });

        priceColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        priceColumn.setOnEditCommit(e -> {
            Flower flower = e.getRowValue();
            flower.setPrice(e.getNewValue());
            flowerTable.edit(-1, null);
        });

        quantityColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTotalQuantity()).asObject());
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        quantityColumn.setOnEditCommit(e -> {
            Flower flower = e.getRowValue();
            flower.setTotalQuantity(e.getNewValue());
            flowerTable.edit(-1, null);
        });

        dateColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPickedDate()));
        dateColumn.setCellFactory(column -> new DatePickerTableCell<>());
        dateColumn.setEditable(true);

        dateColumn.setOnEditCommit(e -> {
            Flower flower = e.getRowValue();
            flower.setPickedDate(e.getNewValue());
            flowerTable.edit(-1, null);
        });
    }


    /**
     * Примусово оновити відображення рядка у таблиці,
     * щоб відобразити внесені зміни у комірках.
     */
    private void refreshRow(Flower flower) {
        int index = flowers.indexOf(flower);
        if (index >= 0) {
            flowers.set(index, flower); // це змусить TableView оновити цей рядок
        }
    }


    private void setupActionColumns() {
        saveColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("✔");
            {
                btn.setOnAction(e -> {
                    Flower flower = getTableView().getItems().get(getIndex());
                    try {
                        if (flower.getId() == 0) {
                            flowerDAO.insert(flower);
                        } else {
                            flowerDAO.update(flower);
                        }
                        loadFlowersByType(flower.getType()); // оновлення
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

        deleteColumn.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("✖");
            {
                btn.setOnAction(e -> {
                    Flower flower = getTableView().getItems().get(getIndex());
                    if (flower.getId() == 0) {
                        flowers.remove(flower); // не збережений — просто прибрати
                    } else {
                        try {
                            flowerDAO.delete(flower);
                            loadFlowersByType(flower.getType());
                        } catch (SQLException ex) {
                            showError("Помилка видалення", ex.getMessage());
                        }
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


}
