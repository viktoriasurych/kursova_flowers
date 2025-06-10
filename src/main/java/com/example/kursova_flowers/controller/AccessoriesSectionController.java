package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.dao.AccessoryTypeDAO;
import com.example.kursova_flowers.dao.*;
import com.example.kursova_flowers.model.Box;
import com.example.kursova_flowers.model.Ribbon;
import com.example.kursova_flowers.model.AccessoryType;
import com.example.kursova_flowers.model.Paper;
import com.example.kursova_flowers.model.GreetingCard;
import com.example.kursova_flowers.model.*;
import com.example.kursova_flowers.util.TableColumnUtil;
import com.example.kursova_flowers.util.TableViewHelper;
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
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
                updateExtraFieldForType(selected.getId());
            }
        });
        setupRowSelection();

    }

    private void setupRowSelection() {
        setupSelectionListener(cardsTable,
                GreetingCard::getText,
                (type, value) -> extraField.setText(value),
                GreetingCard::getType,
                GreetingCard::getColor,
                GreetingCard::getNote,
                boxesTable, ribbonsTable, papersTable);

        setupSelectionListener(boxesTable,
                Box::getBoxType,
                (type, value) -> extraField.setText(value),
                Box::getType,
                Box::getColor,
                Box::getNote,
                cardsTable, ribbonsTable, papersTable);

        setupSelectionListener(ribbonsTable,
                r -> String.valueOf(r.getWidth()),
                (type, value) -> extraField.setText(value),
                Ribbon::getType,
                Ribbon::getColor,
                Ribbon::getNote,
                cardsTable, boxesTable, papersTable);

        setupSelectionListener(papersTable,
                Paper::getMaterial,
                (type, value) -> extraField.setText(value),
                Paper::getType,
                Paper::getColor,
                Paper::getNote,
                cardsTable, boxesTable, ribbonsTable);
    }


    private void setupTables() {
        TableViewHelper.setupReadOnlyTable(cardsTable, greetingCards, new TableViewHelper.ColumnConfig[] {
                new TableViewHelper.ColumnConfig<>(cardTextColumn, GreetingCard::getText, String.class),
                new TableViewHelper.ColumnConfig<>(cardColorColumn, GreetingCard::getColor, String.class),
                new TableViewHelper.ColumnConfig<>(cardPriceColumn, c -> c.getType().getBasePrice(), Double.class),
                new TableViewHelper.ColumnConfig<>(cardNoteColumn, GreetingCard::getNote, String.class)
        });

        TableViewHelper.setupReadOnlyTable(boxesTable, boxes, new TableViewHelper.ColumnConfig[] {
                new TableViewHelper.ColumnConfig<>(boxTypeColumn, Box::getBoxType, String.class),
                new TableViewHelper.ColumnConfig<>(boxColorColumn, Box::getColor, String.class),
                new TableViewHelper.ColumnConfig<>(boxPriceColumn, b -> b.getType().getBasePrice(), Double.class),
                new TableViewHelper.ColumnConfig<>(boxNoteColumn, Box::getNote, String.class)
        });

        TableViewHelper.setupReadOnlyTable(ribbonsTable, ribbons, new TableViewHelper.ColumnConfig[] {
                new TableViewHelper.ColumnConfig<>(ribbonWidthColumn, Ribbon::getWidth, Double.class),
                new TableViewHelper.ColumnConfig<>(ribbonColorColumn, Ribbon::getColor, String.class),
                new TableViewHelper.ColumnConfig<>(ribbonPriceColumn, r -> r.getType().getBasePrice(), Double.class),
                new TableViewHelper.ColumnConfig<>(ribbonNoteColumn, Ribbon::getNote, String.class)
        });

        TableViewHelper. setupReadOnlyTable(papersTable, papers, new TableViewHelper.ColumnConfig[] {
                new TableViewHelper.ColumnConfig<>(paperMaterialColumn, Paper::getMaterial, String.class),
                new TableViewHelper.ColumnConfig<>(paperColorColumn, Paper::getColor, String.class),
                new TableViewHelper.ColumnConfig<>(paperPriceColumn, p -> p.getType().getBasePrice(), Double.class),
                new TableViewHelper.ColumnConfig<>(paperNoteColumn, Paper::getNote, String.class)
        });
    }



    private <T> void setupSelectionListener(TableView<T> table,
                                            Function<T, String> extraFieldValueGetter,
                                            BiConsumer<AccessoryType, String> extraFieldUpdater,
                                            Function<T, AccessoryType> typeGetter,
                                            Function<T, String> colorGetter,
                                            Function<T, String> noteGetter,
                                            TableView<?>... othersToClear) {

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                AccessoryType type = typeGetter.apply(newVal);
                accessoryTypeComboBox.setValue(type);
                updateExtraFieldForType(type.getId());
                extraField.setText(extraFieldValueGetter.apply(newVal));
                colorField.setText(colorGetter.apply(newVal));
                extraInfoArea.setText(noteGetter.apply(newVal));

                for (TableView<?> other : othersToClear) {
                    other.getSelectionModel().clearSelection();
                }
            }
        });
    }


    private void loadAccessoryTypes() {
        try {
            List<AccessoryType> types = accessoryTypeDao.findAll();
            accessoryTypeComboBox.setItems(FXCollections.observableArrayList(types));
            if (!types.isEmpty()) {
                accessoryTypeComboBox.getSelectionModel().selectFirst();
                updateExtraFieldForType(types.get(0).getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private record FieldInfo(String label, String prompt) {}

    // 1. Структура для зберігання інформації про додаткове поле на основі типу
    private static final Map<Integer, FieldInfo> accessoryFieldInfoMap = Map.of(
            1, new FieldInfo("Текст листівки:", "Введіть текст"),
            2, new FieldInfo("Розмір коробки:", "Введіть розмір"),
            3, new FieldInfo("Ширина стрічки:", "Введіть ширину (число)"),
            4, new FieldInfo("Матеріал паперу:", "Введіть матеріал")
    );

    private void updateExtraFieldForType(int typeName) {
        FieldInfo info = accessoryFieldInfoMap.getOrDefault(typeName,
                new FieldInfo("Додаткове поле:", ""));

        extraFieldLabel.setText(info.label);
        extraField.setPromptText(info.prompt);
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
            switch (type.getId()) {
                case 1 -> greetingCards.add(new GreetingCard(type, color, note, extra));
                case 2 -> boxes.add(new Box(type, color, note, extra));
                case 3 ->{
                    double width = Double.parseDouble(extra);
                    ribbons.add(new Ribbon(type, color, note, width));}
                case 4->
                    papers.add(new Paper(type, color, note, extra));
                default->
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

        switch (selected.getId()) {
            case 1-> greetingCards.remove(cardsTable.getSelectionModel().getSelectedItem());
            case 2-> boxes.remove(boxesTable.getSelectionModel().getSelectedItem());
            case 3-> ribbons.remove(ribbonsTable.getSelectionModel().getSelectedItem());
            case 4-> papers.remove(papersTable.getSelectionModel().getSelectedItem());

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
    ObservableList<Accessory> all = FXCollections.observableArrayList();

    public ObservableList<Accessory> getAllAccessories() {
all.clear();
        all.addAll(boxes);
        all.addAll(ribbons);
        all.addAll(papers);
        all.addAll(greetingCards);
        return all;
    }
    public void setAccessories(ObservableList<Accessory> accessories) {
        greetingCards.clear();
        boxes.clear();
        ribbons.clear();
        papers.clear();

        if (accessories != null) {
            for (Accessory acc : accessories) {
                System.out.println(acc.getId());
                if (acc instanceof GreetingCard) {
                    greetingCards.add((GreetingCard) acc); System.out.println(acc.getId());
                } else if (acc instanceof Box) { System.out.println(((Box) acc).getBoxType());
                    boxes.add((Box) acc);
                } else if (acc instanceof Ribbon) { System.out.println(acc.getId());
                    ribbons.add((Ribbon) acc);
                } else if (acc instanceof Paper) { System.out.println(acc.getId());
                    papers.add((Paper) acc);
                }
            }
        }
    }





}
