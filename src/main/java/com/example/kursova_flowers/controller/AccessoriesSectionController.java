package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.dao.*;
import com.example.kursova_flowers.model.*;
import com.example.kursova_flowers.util.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class AccessoriesSectionController {

    private static final Logger logger = LoggerFactory.getLogger(AccessoriesSectionController.class);

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
    private final  ObservableList<Accessory> all = FXCollections.observableArrayList();

    private record FieldInfo(String label, String prompt) {}

    public void setConnection(Connection connection) {
        this.connection = connection;
        this.accessoryTypeDao = new AccessoryTypeDAO(connection);
        logger.info("Встановлено з'єднання з БД та створено AccessoryTypeDAO");
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
                logger.debug("Вибрано тип аксесуару: {} (id={})", selected.getName(), selected.getId());
                updateExtraFieldForType(selected.getId());
            }
        });
        setupRowSelection();

    }

    /**
     * Налаштовує вибір рядків у таблицях аксесуарів,
     * щоб синхронізувати форму з вибраним елементом.
     */
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

    /**
     * Завантажує доступні типи аксесуарів з БД і встановлює їх у ComboBox.
     */
    private void loadAccessoryTypes() {
        try {
            List<AccessoryType> types = accessoryTypeDao.findAll();
            accessoryTypeComboBox.setItems(FXCollections.observableArrayList(types));
            if (!types.isEmpty()) {
                accessoryTypeComboBox.getSelectionModel().selectFirst();
               // updateExtraFieldForType(types.get(0).getId());
            }
            logger.info("Завантажено типи аксесуарів: {}", types.size());
        } catch (SQLException e) {
            logger.error("Помилка завантаження типів аксесуарів з БД", e);
        }
    }


    private static final Map<Integer, FieldInfo> accessoryFieldInfoMap = Map.of(
            1, new FieldInfo("Текст листівки:", "Введіть текст"),
            2, new FieldInfo("Розмір коробки:", "Введіть розмір"),
            3, new FieldInfo("Ширина стрічки:", "Введіть ширину (число)"),
            4, new FieldInfo("Матеріал паперу:", "Введіть матеріал")
    );

    /**
     * Оновлює підпис і вміст додаткового поля форми залежно від типу аксесуару.
     *
     * @param typeName ідентифікатор типу аксесуара
     */
    void updateExtraFieldForType(int typeName) {
        FieldInfo info = accessoryFieldInfoMap.getOrDefault(typeName,
                new FieldInfo("Додаткове поле:", ""));

        extraFieldLabel.setText(info.label);
        extraField.setPromptText(info.prompt);
        extraField.clear();
    }

    void addAccessory() {
        AccessoryType type = accessoryTypeComboBox.getValue();
          if (type == null) {
              logger.warn("Спроба додати аксесуар з порожніми обов'язковими полями");
              return;
          }

        String extra = extraField.getText();
        String color = colorField.getText();
        String note = extraInfoArea.getText();

        if (extra.isBlank() || color.isBlank()) {
            ShowErrorUtil.showAlert("Помилка", "Усі поля мають бути заповнені", Alert.AlertType.ERROR);
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
                default -> {
                    logger.error("Невідомий тип аксесуару: {}", type.getId());
                    ShowErrorUtil.showAlert("Помилка", "Невідомий тип аксесуару", Alert.AlertType.ERROR);
                    return;
                }
            }
            logger.info("Додано аксесуар: тип='{}', extra='{}', color='{}', note='{}'",
                    type.getName(), extra, color, note);
            clearForm();
        } catch (NumberFormatException e) {
            ShowErrorUtil.showAlert("Помилка", "Ширина стрічки має бути числом", Alert.AlertType.ERROR);
        }

    }

    void deleteSelectedAccessory() {
        AccessoryType selected = accessoryTypeComboBox.getValue();

        if (selected == null) {
            logger.warn("Спроба видалити аксесуар без вибору типу");
            return;
        }

        switch (selected.getId()) {
            case 1-> greetingCards.remove(cardsTable.getSelectionModel().getSelectedItem());
            case 2-> boxes.remove(boxesTable.getSelectionModel().getSelectedItem());
            case 3-> ribbons.remove(ribbonsTable.getSelectionModel().getSelectedItem());
            case 4-> papers.remove(papersTable.getSelectionModel().getSelectedItem());

        }
        logger.info("Видалено аксесуар");
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
            logger.info("Встановлення аксесуарів, кількість: {}", accessories.size());
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

    private void clearForm() {
        extraField.clear();
        colorField.clear();
        extraInfoArea.clear();
    }

    /**
     * Налаштовує вибір рядка таблиці аксесуарів.
     * При виборі елемента синхронізує поля форми та очищає вибір інших таблиць.
     *
     * @param table таблиця аксесуарів
     * @param extraFieldValueGetter функція для отримання значення додаткового поля
     * @param extraFieldUpdater функція для оновлення додаткового поля
     * @param typeGetter функція для отримання типу аксесуара
     * @param colorGetter функція для отримання кольору аксесуара
     * @param noteGetter функція для отримання примітки аксесуара
     * @param othersToClear інші таблиці, вибір у яких треба очистити
     * @param <T> тип аксесуара
     */
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

                // Достатньо одного setValue для ComboBox
                // Очищуємо вибір перед оновленням
                accessoryTypeComboBox.getSelectionModel().clearSelection();
                // Встановлюємо нове значення
                accessoryTypeComboBox.setValue(type);


                // Оновлюємо додаткове поле, якщо треба
                updateExtraFieldForType(type.getId());

                // Встановлюємо значення інших полів
                extraField.setText(extraFieldValueGetter.apply(newVal));
                colorField.setText(colorGetter.apply(newVal));
                extraInfoArea.setText(noteGetter.apply(newVal));

                // Очищаємо вибір в інших таблицях
                for (TableView<?> other : othersToClear) {
                    other.getSelectionModel().clearSelection();
                }
            }
        });

        // За бажанням, якщо таблиця не пуста, одразу вибираємо перший рядок
        if (!table.getItems().isEmpty()) {
            table.getSelectionModel().selectFirst();
        }
    }

}
