package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.dao.AccessoryTypeDAO;
import com.example.kursova_flowers.dao.*;
import com.example.kursova_flowers.model.Box;
import com.example.kursova_flowers.model.Ribbon;
import com.example.kursova_flowers.model.AccessoryType;
import com.example.kursova_flowers.model.Paper;
import com.example.kursova_flowers.model.GreetingCard;
import com.example.kursova_flowers.model.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccessoriesSectionController {

    @FXML private ComboBox<AccessoryType> accessoryTypeComboBox;
    @FXML private Label extraFieldLabel;
    @FXML private TextField extraField;
    @FXML private TextField colorField;
    @FXML private TextArea extraInfoArea;
    @FXML private Button addButton;
    @FXML private Button deleteButton;

    @FXML private TableView<GreetingCard> cardsTable;
    @FXML private TableColumn<GreetingCard, String> cardTextColumn;
    @FXML private TableColumn<GreetingCard, String> cardColorColumn;
    @FXML private TableColumn<GreetingCard, Double> cardPriceColumn;
    @FXML private TableColumn<GreetingCard, String> cardNoteColumn;

    @FXML private TableView<Box> boxesTable;
    @FXML private TableColumn<Box, String> boxTypeColumn;
    @FXML private TableColumn<Box, String> boxColorColumn;
    @FXML private TableColumn<Box, Double> boxPriceColumn;
    @FXML private TableColumn<Box, String> boxNoteColumn;

    @FXML private TableView<Ribbon> ribbonsTable;
    @FXML private TableColumn<Ribbon, Double> ribbonWidthColumn;
    @FXML private TableColumn<Ribbon, String> ribbonColorColumn;
    @FXML private TableColumn<Ribbon, Double> ribbonPriceColumn;
    @FXML private TableColumn<Ribbon, String> ribbonNoteColumn;

    @FXML private TableView<Paper> papersTable;
    @FXML private TableColumn<Paper, String> paperMaterialColumn;
    @FXML private TableColumn<Paper, String> paperColorColumn;
    @FXML private TableColumn<Paper, Double> paperPriceColumn;
    @FXML private TableColumn<Paper, String> paperNoteColumn;

    private Connection connection;
    private AccessoryTypeDAO accessoryTypeDao;

    private final ObservableList<GreetingCard> greetingCards = FXCollections.observableArrayList();
    private final ObservableList<Box> boxes = FXCollections.observableArrayList();
    private final ObservableList<Ribbon> ribbons = FXCollections.observableArrayList();
    private final ObservableList<Paper> papers = FXCollections.observableArrayList();

    public void setConnection(Connection connection) {
        this.connection = connection;
        this.accessoryTypeDao = new AccessoryTypeDAO(connection);
        loadAccessoryTypes();
    }

    @FXML
    public void initialize() {
        setupTables();

        addButton.setOnAction(e -> addAccessory());
        deleteButton.setOnAction(e -> deleteSelectedAccessory());

        accessoryTypeComboBox.setOnAction(e -> {
            AccessoryType selected = accessoryTypeComboBox.getValue();
            if (selected != null) {
                updateExtraFieldForType(selected.getName());
            }
        });
        setupRowSelection();

    }
    private void setupRowSelection() {
        cardsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                accessoryTypeComboBox.setValue(newVal.getType());
                updateExtraFieldForType(newVal.getType().getName());
                extraField.setText(newVal.getText());
                colorField.setText(newVal.getColor());
                extraInfoArea.setText(newVal.getNote());

                // Очистити інші вибори
                boxesTable.getSelectionModel().clearSelection();
                ribbonsTable.getSelectionModel().clearSelection();
                papersTable.getSelectionModel().clearSelection();
            }
        });

        boxesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                accessoryTypeComboBox.setValue(newVal.getType());
                updateExtraFieldForType(newVal.getType().getName());
                extraField.setText(newVal.getBoxType());
                colorField.setText(newVal.getColor());
                extraInfoArea.setText(newVal.getNote());

                // Очистити інші вибори
                cardsTable.getSelectionModel().clearSelection();
                ribbonsTable.getSelectionModel().clearSelection();
                papersTable.getSelectionModel().clearSelection();
            }
        });

        ribbonsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                accessoryTypeComboBox.setValue(newVal.getType());
                updateExtraFieldForType(newVal.getType().getName());
                extraField.setText(String.valueOf(newVal.getWidth()));
                colorField.setText(newVal.getColor());
                extraInfoArea.setText(newVal.getNote());

                // Очистити інші вибори
                cardsTable.getSelectionModel().clearSelection();
                boxesTable.getSelectionModel().clearSelection();
                papersTable.getSelectionModel().clearSelection();
            }
        });

        papersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                accessoryTypeComboBox.setValue(newVal.getType());
                updateExtraFieldForType(newVal.getType().getName());
                extraField.setText(newVal.getMaterial());
                colorField.setText(newVal.getColor());
                extraInfoArea.setText(newVal.getNote());

                // Очистити інші вибори
                cardsTable.getSelectionModel().clearSelection();
                boxesTable.getSelectionModel().clearSelection();
                ribbonsTable.getSelectionModel().clearSelection();
            }
        });

    }

    private void setupTables() {
        cardsTable.setItems(greetingCards);
        boxesTable.setItems(boxes);
        ribbonsTable.setItems(ribbons);
        papersTable.setItems(papers);

        cardTextColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getText()));
        cardColorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getColor()));
        cardPriceColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getType().getBasePrice()).asObject());

        cardNoteColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNote()));

        boxTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBoxType()));
        boxColorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getColor()));
        boxPriceColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getType().getBasePrice()).asObject());
        boxNoteColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNote()));


        ribbonWidthColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getWidth()).asObject());
        ribbonColorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getColor()));
        ribbonPriceColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getType().getBasePrice()).asObject());
        ribbonNoteColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNote()));


        paperMaterialColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaterial()));
        paperColorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getColor()));
        paperPriceColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getType().getBasePrice()).asObject());
        paperNoteColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNote()));

    }

    private void loadAccessoryTypes() {
        try {
            List<AccessoryType> types = accessoryTypeDao.findAll();
            accessoryTypeComboBox.setItems(FXCollections.observableArrayList(types));
            if (!types.isEmpty()) {
                accessoryTypeComboBox.getSelectionModel().selectFirst();
                updateExtraFieldForType(types.get(0).getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateExtraFieldForType(String typeName) {

        switch (typeName) {
            case "листівка":
                extraFieldLabel.setText("Текст листівки:");
                extraField.setPromptText("Введіть текст");

                break;
            case "коробка":
                extraFieldLabel.setText("Розмір коробки:");
                extraField.setPromptText("Введіть розмір");

                break;
            case "стрічка":
                extraFieldLabel.setText("Ширина стрічки:");
                extraField.setPromptText("Введіть ширину (число)");

                break;
            case "папір":
                extraFieldLabel.setText("Матеріал паперу:");
                extraField.setPromptText("Введіть матеріал");

                break;
            default:
                extraFieldLabel.setText("Додаткове поле:");
                extraField.setPromptText("");
        }

        extraField.clear();
    }

    private void addAccessory() {
        AccessoryType type = accessoryTypeComboBox.getValue();
        if (type == null) return;

        String extra = extraField.getText();
        String color = colorField.getText();
        String note = extraInfoArea.getText();

        if (extra.isBlank() || color.isBlank()) {
            showAlert("Помилка", "Усі поля мають бути заповнені", Alert.AlertType.WARNING);
            return;
        }

        try {
            switch (type.getName()) {
                case "листівка":
                    greetingCards.add(new GreetingCard(type, color, note, extra));
                    break;
                case "коробка":
                    boxes.add(new Box(type, color, note, extra));
                    break;
                case "стрічка":
                    double width = Double.parseDouble(extra);
                    ribbons.add(new Ribbon(type, color, note, width));
                    break;
                case "папір":
                    papers.add(new Paper(type, color, note, extra));
                    break;
                default:
                    showAlert("Помилка", "Невідомий тип аксесуару", Alert.AlertType.ERROR);
            }
            clearForm();
        } catch (NumberFormatException e) {
            showAlert("Помилка", "Ширина стрічки має бути числом", Alert.AlertType.ERROR);
        }

        System.out.println("Додаємо аксесуар:");
        System.out.println("Тип: " + type.getName());
        System.out.println("Extra: '" + extra + "'");
        System.out.println("Color: '" + color + "'");
        System.out.println("Note: '" + note + "'");

    }

    private void deleteSelectedAccessory() {
        AccessoryType selected = accessoryTypeComboBox.getValue();

        if (selected == null) return;

        switch (selected.getName()) {
            case "листівка":
                greetingCards.remove(cardsTable.getSelectionModel().getSelectedItem());
                break;
            case "коробка":
                boxes.remove(boxesTable.getSelectionModel().getSelectedItem());
                break;
            case "стрічка":
                ribbons.remove(ribbonsTable.getSelectionModel().getSelectedItem());
                break;
            case "папір":
                papers.remove(papersTable.getSelectionModel().getSelectedItem());
                break;
        }
    }

    private void clearForm() {
        extraField.clear();
        colorField.clear();
        extraInfoArea.clear();
    }

    private void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public ObservableList<GreetingCard> getGreetingCards() {
        return greetingCards;
    }

    public ObservableList<Box> getBoxes() {
        return boxes;
    }

    public ObservableList<Ribbon> getRibbons() {
        return ribbons;
    }

    public ObservableList<Paper> getPapers() {
        return papers;
    }

    public ObservableList<Accessory> getAllAccessories() {
        ObservableList<Accessory> all = FXCollections.observableArrayList();
        all.addAll(boxes);
        all.addAll(ribbons);
        all.addAll(papers);
        all.addAll(greetingCards);
        return all;
    }

}
