package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.dao.FlowerDAO;
import com.example.kursova_flowers.dao.FlowerTypeDAO;
import com.example.kursova_flowers.model.Flower;
import com.example.kursova_flowers.model.FlowerInBouquet;
import com.example.kursova_flowers.model.FlowerType;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FlowersSectionController {

    @FXML
    private TableView<FlowerInBouquet> flowersInBouquetTable;

    @FXML
    private TableColumn<FlowerInBouquet, String> colFlowerType;

    @FXML
    private TableColumn<FlowerInBouquet, String> colFlowerName;

    @FXML
    private TableColumn<FlowerInBouquet, Integer> colQuantity;

    @FXML
    private TableColumn<FlowerInBouquet, Double> colStemLength;

    @FXML
    private TableColumn<FlowerInBouquet, Double> colPrice;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private ComboBox<FlowerType> flowerTypeComboBox;

    @FXML
    private ComboBox<Flower> flowerComboBox;

    @FXML
    private Spinner<Integer> quantitySpinner;

    @FXML
    private TextField stemLengthField;

    @FXML
    private Button addFlowerButton;

    @FXML
    private Button removeFlowerButton;

    private ObservableList<FlowerInBouquet> flowersInBouquet = FXCollections.observableArrayList();

    private FlowerTypeDAO flowerTypeDAO;
    private FlowerDAO flowerDAO;

    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
        flowerTypeDAO = new FlowerTypeDAO(connection);
        flowerDAO = new FlowerDAO(connection);
        initializeData();
    }

    @FXML
    public void initialize() {
        colFlowerType.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFlower().getType().getName()
        ));
        colFlowerName.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getFlower().getName()
        ));
        colQuantity.setCellValueFactory(cellData -> new SimpleIntegerProperty(
                cellData.getValue().getQuantity()
        ).asObject());
        colStemLength.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                cellData.getValue().getStemLength()
        ).asObject());
        colPrice.setCellValueFactory(cellData -> new SimpleDoubleProperty(
                cellData.getValue().getQuantity() * cellData.getValue().getFlower().getPrice()
        ).asObject());

        flowersInBouquetTable.setItems(flowersInBouquet);

        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1));

        addFlowerButton.setOnAction(e -> onAddFlower());
        removeFlowerButton.setOnAction(e -> onRemoveFlower());

        flowerTypeComboBox.setOnAction(e -> onFlowerTypeSelected());

        stemLengthField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d*)?")) {
                stemLengthField.setText(oldVal);
            }
        });

        flowersInBouquetTable.setOnMouseClicked(event -> {
            FlowerInBouquet selected = flowersInBouquetTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Flower selectedFlower = selected.getFlower();
                FlowerType selectedType = selectedFlower.getType();

                flowerTypeComboBox.getSelectionModel().select(selectedType);

                // Після вибору типу завантажаться квітки — додай таймер, щоб трохи зачекати
                Platform.runLater(() -> {
                    // Повторно завантажити квітки вручну (як у onFlowerTypeSelected)
                    try {
                        List<Flower> flowers = flowerDAO.findByType(selectedType);
                        flowerComboBox.setItems(FXCollections.observableArrayList(flowers));

                        // Вибрати потрібну квітку вручну після оновлення списку
                        flowerComboBox.getSelectionModel().select(
                                flowers.stream()
                                        .filter(f -> f.getId() == selectedFlower.getId())
                                        .findFirst().orElse(null)
                        );

                        // Встановити значення кількості та довжини
                        int maxQuantity = selectedFlower.getTotalQuantity();
                        int alreadyAdded = flowersInBouquet.stream()
                                .filter(fib -> fib.getFlower().getId() == selectedFlower.getId())
                                .mapToInt(FlowerInBouquet::getQuantity)
                                .sum();

                        int availableQuantity = Math.max(1, maxQuantity - alreadyAdded + selected.getQuantity());
                        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, availableQuantity, selected.getQuantity()));

                        stemLengthField.setText(String.valueOf(selected.getStemLength()));

                    } catch (SQLException e) {
                        showError("Помилка при оновленні даних для редагування: " + e.getMessage());
                    }
                });
            }
        });


    }



    private void initializeData() {
        try {
            List<FlowerType> flowerTypes = flowerTypeDAO.findAll();
            flowerTypeComboBox.setItems(FXCollections.observableArrayList(flowerTypes));

            if (!flowerTypes.isEmpty()) {
                flowerTypeComboBox.getSelectionModel().select(0);
                onFlowerTypeSelected();
            }
        } catch (SQLException e) {
            showError("Помилка при завантаженні типів квітів: " + e.getMessage());
        }
    }

    private void onFlowerTypeSelected() {
        FlowerType selectedType = flowerTypeComboBox.getSelectionModel().getSelectedItem();
        if (selectedType == null) {
            flowerComboBox.setItems(FXCollections.observableArrayList());
            quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,1,1));
            return;
        }
        try {
            List<Flower> flowers = flowerDAO.findByType(selectedType);
            flowerComboBox.setItems(FXCollections.observableArrayList(flowers));
            if (!flowers.isEmpty()) {
                flowerComboBox.getSelectionModel().select(0);
                onFlowerSelected();
            } else {
                quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1));
            }
        } catch (SQLException e) {
            showError("Помилка при завантаженні квіток: " + e.getMessage());
        }

        flowerComboBox.setOnAction(e -> onFlowerSelected());
    }

    private void onFlowerSelected() {
        Flower selectedFlower = flowerComboBox.getSelectionModel().getSelectedItem();
        if (selectedFlower == null) {
            quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1, 1));
            return;
        }
        int maxQuantity = selectedFlower.getTotalQuantity();

        int alreadyAdded = flowersInBouquet.stream()
                .filter(fib -> fib.getFlower().getId() == selectedFlower.getId())
                .mapToInt(FlowerInBouquet::getQuantity)
                .sum();

        int availableQuantity = maxQuantity - alreadyAdded;
        if (availableQuantity < 1) availableQuantity = 1;

        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, availableQuantity, 1));
    }

    private void onAddFlower() {
        Flower selectedFlower = flowerComboBox.getSelectionModel().getSelectedItem();
        if (selectedFlower == null) {
            showError("Виберіть квітку.");
            return;
        }

        int quantity = quantitySpinner.getValue();
        if (quantity <= 0) {
            showError("Кількість має бути більшою за 0.");
            return;
        }

        double stemLength;
        try {
            stemLength = Double.parseDouble(stemLengthField.getText());
            if (stemLength <= 0) {
                showError("Довжина стебла має бути більшою за 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Неправильне значення довжини стебла.");
            return;
        }

        // Якщо є вибраний рядок — оновлюємо
        FlowerInBouquet selectedRow = flowersInBouquetTable.getSelectionModel().getSelectedItem();
        if (selectedRow != null && selectedRow.getFlower().getId() == selectedFlower.getId()) {
            int maxQuantity = selectedFlower.getTotalQuantity();
            int totalWithoutCurrent = flowersInBouquet.stream()
                    .filter(fib -> fib != selectedRow && fib.getFlower().getId() == selectedFlower.getId())
                    .mapToInt(FlowerInBouquet::getQuantity)
                    .sum();

            if (quantity + totalWithoutCurrent > maxQuantity) {
                showError("Перевищено доступну кількість квіток.");
                return;
            }

            selectedRow.setQuantity(quantity);
            selectedRow.setStemLength(stemLength);
            flowersInBouquetTable.refresh();
            flowersInBouquetTable.getSelectionModel().clearSelection(); // скидаємо вибір
        } else {
            // Створити новий запис
            Optional<FlowerInBouquet> existing = flowersInBouquet.stream()
                    .filter(fib -> fib.getFlower().getId() == selectedFlower.getId() && fib.getStemLength() == stemLength)
                    .findFirst();

            if (existing.isPresent()) {
                FlowerInBouquet fib = existing.get();
                int newQuantity = fib.getQuantity() + quantity;
                int maxQuantity = selectedFlower.getTotalQuantity();
                if (newQuantity > maxQuantity) {
                    showError("Перевищено доступну кількість квіток.");
                    return;
                }
                fib.setQuantity(newQuantity);
                flowersInBouquetTable.refresh();
            } else {
                FlowerInBouquet newFib = new FlowerInBouquet();
                newFib.setFlower(selectedFlower);
                newFib.setQuantity(quantity);
                newFib.setStemLength(stemLength);
                flowersInBouquet.add(newFib);
            }
        }

        updateTotalPrice();

    }


    private void onRemoveFlower() {
        FlowerInBouquet selected = flowersInBouquetTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Виберіть квітку в таблиці для видалення.");
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public ObservableList<FlowerInBouquet> getFlowersInBouquet() {
        return flowersInBouquet;
    }

    public void setFlowersInBouquet(ObservableList<FlowerInBouquet> flowers) {
        flowersInBouquet.clear();
        if (flowers != null) {
            flowersInBouquet.addAll(flowers);
        }
        updateTotalPrice();  // додано
    }
}
