package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.dao.*;
import com.example.kursova_flowers.model.*;
import com.example.kursova_flowers.service.*;
import com.example.kursova_flowers.util.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlowersSectionController {

    private static final Logger logger = Logger.getLogger(FlowersSectionController.class.getName());

    @FXML private TableView<FlowerInBouquet> flowersInBouquetTable;
    @FXML private TableColumn<FlowerInBouquet, String> colFlowerType;
    @FXML private TableColumn<FlowerInBouquet, String> colFlowerName;
    @FXML private TableColumn<FlowerInBouquet, Integer> colQuantity;
    @FXML private TableColumn<FlowerInBouquet, String> colFreshness;
    @FXML private TableColumn<FlowerInBouquet, Double> colStemLength;
    @FXML private TableColumn<FlowerInBouquet, Double> colPrice;

    @FXML private Label totalPriceLabel;
    @FXML private ComboBox<FlowerType> flowerTypeComboBox;
    @FXML private ComboBox<Flower> flowerComboBox;
    @FXML private Spinner<Integer> quantitySpinner;
    @FXML private TextField stemLengthField;

    @FXML private Button addFlowerButton;
    @FXML private Button removeFlowerButton;
    @FXML private Button sortByFreshnessButton;
    @FXML private TextField minLengthField;
    @FXML private TextField maxLengthField;
    @FXML private Button findByLengthButton;
    @FXML private Button resetFilterButton;

    private ObservableList<FlowerInBouquet> flowersInBouquet = FXCollections.observableArrayList();
    private List<FlowerInBouquet> originalFlowersList;

    private FlowerTypeDAO flowerTypeDAO;
    private FlowerDAO flowerDAO;

    private FlowersFiltrService filtrService = new FlowersFiltrService();

    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
        flowerTypeDAO = new FlowerTypeDAO(connection);
        flowerDAO = new FlowerDAO(connection);
        initializeData();
    }

    @FXML
    public void initialize() {
        setupTableColumns();

        flowersInBouquetTable.setItems(flowersInBouquet);

        setupQuantitySpinner();

        addFlowerButton.setOnAction(e -> onAddFlower());
        removeFlowerButton.setOnAction(e -> onRemoveFlower());

        flowerTypeComboBox.setOnAction(e -> onFlowerTypeSelected());

        setupStemLengthFieldValidation();

        setupTableSelectionListener();

        originalFlowersList = FXCollections.observableArrayList();

        findByLengthButton.setOnAction(e -> onFindByLength());
        resetFilterButton.setOnAction(e -> onResetFilter());
    }

    private void initializeData() {
        try {
            List<FlowerType> flowerTypes = flowerTypeDAO.findAll();
            flowerTypeComboBox.setItems(FXCollections.observableArrayList(flowerTypes));
            logger.info("Типи квітів завантажено: " + flowerTypes.size());
            if (!flowerTypes.isEmpty()) {
                flowerTypeComboBox.getSelectionModel().select(0);
                onFlowerTypeSelected();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Помилка при завантаженні типів квітів", e);
            ShowErrorUtil.showAlert("Помилка при завантаженні типів квітів: " , e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupTableColumns() {

        TableColumnUtil.makeReadOnlyStringColumn(colFlowerType, fib -> fib.getFlower().getType().getName());
        TableColumnUtil.makeReadOnlyStringColumn(colFlowerName, fib -> fib.getFlower().getName());
        TableColumnUtil.makeReadOnlyIntegerColumn(colQuantity, FlowerInBouquet::getQuantity);
        TableColumnUtil.makeReadOnlyDoubleColumn(colStemLength, FlowerInBouquet::getStemLength);

        colPrice.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(
                        cellData.getValue().getQuantity() * cellData.getValue().getFlower().getPrice()
                ).asObject()
        );
        colPrice.setEditable(false);

        colFreshness.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getFlower().getPickedDate();
            String value = (date != null) ? date.toString() : "";
            return new SimpleStringProperty(value);
        });
        colFreshness.setEditable(false);

        sortByFreshnessButton.setOnAction(e -> onSortByFreshness());
    }

    private void setupQuantitySpinner() {
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));
    }

    private void setupStemLengthFieldValidation() {
        stemLengthField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                stemLengthField.setText(oldVal);
            }
        });
    }

    /**
     * Встановлює вибір рядка у таблиці квітів.
     */
    private void setupTableSelectionListener() {
        flowersInBouquetTable.setOnMouseClicked(event -> {
            FlowerInBouquet selected = flowersInBouquetTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                updateControlsForSelectedFlowerInBouquet(selected);
            }
        });
    }

    /**
     * Оновлює елементи керування згідно з вибраним у таблиці квітом.
     * @param selected вибрана квітка у букеті
     */
    private void updateControlsForSelectedFlowerInBouquet(FlowerInBouquet selected) {
        Flower selectedFlower = selected.getFlower();
        FlowerType selectedType = selectedFlower.getType();

        flowerTypeComboBox.getSelectionModel().select(selectedType);

        Platform.runLater(() -> {
            try {
                List<Flower> flowers = flowerDAO.findByType(selectedType);
                flowerComboBox.setItems(FXCollections.observableArrayList(flowers));
                flowerComboBox.getSelectionModel().select(
                        flowers.stream()
                                .filter(f -> f.getId() == selectedFlower.getId())
                                .findFirst()
                                .orElse(null)
                );

                quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, selected.getQuantity()));
                stemLengthField.setText(String.valueOf(selected.getStemLength()));

            } catch (SQLException e) {
                ShowErrorUtil.showAlert("Помилка при оновленні даних для редагування: ", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void onSortByFreshness() {
        FlowersSortService.sortByFreshness(flowersInBouquet);
        logger.info("Застосовано сортування по свіжості");
    }

    private void onFindByLength() {
        Double minLength = parseDoubleOrNull(minLengthField.getText());
        Double maxLength = parseDoubleOrNull(maxLengthField.getText());

        if (minLength != null && maxLength != null && minLength > maxLength) {
            ShowErrorUtil.showAlert("помилка","Мінімальна довжина не може бути більшою за максимальну.", Alert.AlertType.ERROR);
            return;
        }

        List<FlowerInBouquet> filtered = filtrService.filterByStemLengthRange(originalFlowersList, minLength, maxLength);
        logger.info("Застосовано фільтр по довжині стебла. Кількість знайдених: " + filtered.size());
        flowersInBouquet.setAll(filtered);
    }

    private void onResetFilter() {
        minLengthField.clear();
        maxLengthField.clear();
        flowersInBouquet.setAll(originalFlowersList);
    }

    /**
     * Парсить рядок у Double, повертає null, якщо парсинг неуспішний.
     * @param text текст для парсингу
     * @return число або null
     */
    private Double parseDoubleOrNull(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Обробляє подію вибору типу квітів у комбобоксі.
     */
    private void onFlowerTypeSelected() {
        FlowerType selectedType = flowerTypeComboBox.getSelectionModel().getSelectedItem();
        if (selectedType == null) {
            logger.warning("Тип квітки не вибрано.");
            flowerComboBox.setItems(FXCollections.observableArrayList());
            quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,1,1));
            return;
        }
        try {
            List<Flower> flowers = flowerDAO.findByType(selectedType);
            flowerComboBox.setItems(FXCollections.observableArrayList(flowers));
            logger.info("Квітки типу '" + selectedType.getName() + "' завантажено: " + flowers.size());

            if (!flowers.isEmpty()) {
                flowerComboBox.getSelectionModel().select(0);
                onFlowerSelected();
            } else {
                quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Помилка при завантаженні квіток", e);
            ShowErrorUtil.showAlert("Помилка при завантаженні квіток: " , e.getMessage(), Alert.AlertType.ERROR);
        }

        flowerComboBox.setOnAction(e -> onFlowerSelected());
    }

    /**
     * Обробляє подію вибору квітки у комбобоксі.
     */
    private void onFlowerSelected() {
        Flower selectedFlower = flowerComboBox.getSelectionModel().getSelectedItem();
        if (selectedFlower == null) {
            quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1));
            return;
        }


        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));
    }

    private void onAddFlower() {
        Flower selectedFlower = flowerComboBox.getSelectionModel().getSelectedItem();
        if (selectedFlower == null) {
            ShowErrorUtil.showAlert("помилка", "Виберіть квітку.", Alert.AlertType.ERROR);
            logger.warning("Спроба додати квітку без вибору.");
            return;
        }

        int quantity = quantitySpinner.getValue();
        if (quantity <= 0) {
            ShowErrorUtil.showAlert("помилка", "Кількість має бути більшою за 0.", Alert.AlertType.ERROR);
            logger.warning("Невірна кількість при додаванні квітки: " + quantity);
            return;
        }

        double stemLength;
        try {
            stemLength = Double.parseDouble(stemLengthField.getText());
            if (stemLength <= 0) {
                logger.warning("Невірна довжина стебла при додаванні квітки: " + stemLengthField.getText());
                ShowErrorUtil.showAlert("помилка", "Довжина стебла має бути більшою за 0.", Alert.AlertType.ERROR);
                return;
            }
        } catch (NumberFormatException e) {
            ShowErrorUtil.showAlert("помилка", "Неправильне значення довжини стебла.", Alert.AlertType.ERROR);
            logger.warning("Помилка парсингу довжини стебла: " + stemLengthField.getText());
            return;
        }

        FlowerInBouquet selectedRow = flowersInBouquetTable.getSelectionModel().getSelectedItem();
        if (selectedRow != null && selectedRow.getFlower().getId() == selectedFlower.getId()) {

            selectedRow.setQuantity(quantity);
            selectedRow.setStemLength(stemLength);
            flowersInBouquetTable.refresh();
            flowersInBouquetTable.getSelectionModel().clearSelection(); // скидаємо вибір
        } else {

            Optional<FlowerInBouquet> existing = flowersInBouquet.stream()
                    .filter(fib -> fib.getFlower().getId() == selectedFlower.getId() && fib.getStemLength() == stemLength)
                    .findFirst();

            if (existing.isPresent()) {
                FlowerInBouquet fib = existing.get();
                int newQuantity = fib.getQuantity() + quantity;

                fib.setQuantity(newQuantity);
                flowersInBouquetTable.refresh();
            } else {
                FlowerInBouquet newFib = new FlowerInBouquet();
                newFib.setFlower(selectedFlower);
                newFib.setQuantity(quantity);
                newFib.setStemLength(stemLength);
                flowersInBouquet.add(newFib);
            }
            logger.info("Квітку додано до букету: " + selectedFlower.getName() +
                    ", Кількість: " + quantity + ", Довжина стебла: " + stemLength);
        }

        updateTotalPrice();
        updateOriginalListSnapshot();
    }

    private void onRemoveFlower() {
        FlowerInBouquet selected = flowersInBouquetTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            ShowErrorUtil.showAlert("помилка", "Виберіть квітку в таблиці для видалення.", Alert.AlertType.ERROR);
            return;
        }
        flowersInBouquet.remove(selected);
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double total = flowersInBouquet.stream()
                .mapToDouble(fib -> fib.getQuantity() * fib.getFlower().getPrice())
                .sum();
        totalPriceLabel.setText(String.format("Загальна ціна: %.2f грн", total));
    }

    public ObservableList<FlowerInBouquet> getFlowersInBouquet() {
        return flowersInBouquet;
    }

    public void setFlowersInBouquet(ObservableList<FlowerInBouquet> flowers) {
        flowersInBouquet.clear();
        if (flowers != null) {
            flowersInBouquet.addAll(flowers);
        }
        updateTotalPrice();
        updateOriginalListSnapshot();
    }

    private void updateOriginalListSnapshot() {
        originalFlowersList = FXCollections.observableArrayList(flowersInBouquet);
    }
}
