package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.dao.*;
import com.example.kursova_flowers.dao.FlowerDAO;
import com.example.kursova_flowers.dao.FlowerTypeDAO;
import com.example.kursova_flowers.model.*;
import com.example.kursova_flowers.service.BouquetCalculatorService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.SQLException;

public class ReceiptSectionController {
    @FXML private VBox flowersSection;
    @FXML private VBox cardsSection;
    @FXML private VBox boxesSection;
    @FXML private VBox ribbonsSection;
    @FXML private VBox papersSection;

    @FXML
    private Label bouquetNameLabel;

    @FXML
    private TableView<FlowerInBouquet> flowersTable;
    @FXML
    private TableColumn<FlowerInBouquet, String> flowerTypeColumn;
    @FXML
    private TableColumn<FlowerInBouquet, String> flowerNameColumn;
    @FXML
    private TableColumn<FlowerInBouquet, Double> flowerStemLengthColumn;
    @FXML
    private TableColumn<FlowerInBouquet, Integer> flowerQuantityColumn;
    @FXML
    private TableColumn<FlowerInBouquet, Double> flowerTotalPriceColumn;

    @FXML
    private TableView<GreetingCard> cardsTable;
    @FXML
    private TableColumn<GreetingCard, String> cardTextColumn;
    @FXML
    private TableColumn<GreetingCard, String> cardColorColumn;
    @FXML
    private TableColumn<GreetingCard, Double> cardPriceColumn;

    @FXML
    private TableView<Box> boxesTable;
    @FXML
    private TableColumn<Box, String> boxTypeColumn;
    @FXML
    private TableColumn<Box, String> boxColorColumn;
    @FXML
    private TableColumn<Box, Double> boxPriceColumn;

    @FXML
    private TableView<Ribbon> ribbonsTable;
    @FXML
    private TableColumn<Ribbon, Double> ribbonWidthColumn;
    @FXML
    private TableColumn<Ribbon, String> ribbonColorColumn;
    @FXML
    private TableColumn<Ribbon, Double> ribbonPriceColumn;

    @FXML
    private TableView<Paper> papersTable;
    @FXML
    private TableColumn<Paper, String> paperMaterialColumn;
    @FXML
    private TableColumn<Paper, String> paperColorColumn;
    @FXML
    private TableColumn<Paper, Double> paperPriceColumn;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Button printButton;

    // Контролери секцій - встановлюються ззовні (з BouquetFormController або іншого)
    private FlowersSectionController flowersController;
    private AccessoriesSectionController accessoriesController;
    private BouquetFormController bouquetFormController;

    private BouquetCalculatorService calculatorService = new BouquetCalculatorService();
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;

    }

    public void bindBouquetName(TextField nameField) {
        bouquetNameLabel.textProperty().bind(nameField.textProperty());
    }

    @FXML
    public void initialize() {

        // Налаштування колонок квітів
        flowerTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFlower().getType().getName()));
        flowerNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFlower().getName()));
        flowerStemLengthColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getStemLength()).asObject());;
        flowerQuantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());;
        flowerTotalPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getQuantity() * cellData.getValue().getFlower().getPrice()).asObject());;


        // Колонки листівок
        cardTextColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getText()));
        cardColorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getColor()));
        cardPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getType().getBasePrice()).asObject());

        // Колонки коробок
        boxTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBoxType()));
        boxColorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getColor()));
        boxPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getType().getBasePrice()).asObject());

        // Колонки стрічок
        ribbonWidthColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getWidth()).asObject());
        ribbonColorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getColor()));
        ribbonPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getType().getBasePrice()).asObject());


        // Колонки паперу
        paperMaterialColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaterial()));
        paperColorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getColor()));
        paperPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getType().getBasePrice()).asObject());



        // Кнопки
        saveButton.setOnAction(event -> onSave());
        printButton.setOnAction(event -> onPrint());
    }

    /**
     * Встановити контролери секцій для отримання локальних списків і назви букета
     */
    public void setControllers(FlowersSectionController flowersController,
                               AccessoriesSectionController accessoriesController,
                               BouquetFormController bouquetFormController) {
        this.flowersController = flowersController;
        this.accessoriesController = accessoriesController;
        this.bouquetFormController = bouquetFormController;


        updateDataFromControllers();
    }
    private void bindTableHeight(TableView<?> table) {
        table.setFixedCellSize(25); // або 24 — залежно від вашої теми
        table.prefHeightProperty().bind(
                Bindings.size(table.getItems())
                        .multiply(table.getFixedCellSize())
                        .add(30) // додатково для заголовка таблиці
        );
    }

    /**
     * Оновити дані з контролерів у чек-вікні
     */
    private void updateDataFromControllers() {
        if (flowersController == null || accessoriesController == null || bouquetFormController == null) {
            return;
        }
        bindTableHeight(flowersTable);
        bindTableHeight(cardsTable);
        bindTableHeight(boxesTable);
        bindTableHeight(ribbonsTable);
        bindTableHeight(papersTable);

        // Квіти
        ObservableList<FlowerInBouquet> flowers = flowersController.getFlowersInBouquet();
        flowersTable.setItems(flowers);
        flowersSection.setVisible(!flowers.isEmpty());
        flowersSection.setManaged(!flowers.isEmpty());

        // Аксесуари
        cardsTable.setItems(accessoriesController.getGreetingCards());
        cardsSection.setVisible(!accessoriesController.getGreetingCards().isEmpty());
        cardsSection.setManaged(!accessoriesController.getGreetingCards().isEmpty());

        boxesTable.setItems(accessoriesController.getBoxes());
        boxesSection.setVisible(!accessoriesController.getBoxes().isEmpty());
        boxesSection.setManaged(!accessoriesController.getBoxes().isEmpty());

        ribbonsTable.setItems(accessoriesController.getRibbons());
        ribbonsSection.setVisible(!accessoriesController.getRibbons().isEmpty());
        ribbonsSection.setManaged(!accessoriesController.getRibbons().isEmpty());

        papersTable.setItems(accessoriesController.getPapers());
        papersSection.setVisible(!accessoriesController.getPapers().isEmpty());
        papersSection.setManaged(!accessoriesController.getPapers().isEmpty());


        // Порахувати загальну ціну

        Bouquet bouquet = new Bouquet();
        bouquet.setFlowers(flowersController.getFlowersInBouquet());
        bouquet.setAccessories(accessoriesController.getAllAccessories());

        System.out.println();
        double total = calculatorService.calculateTotalPrice(bouquet);


        totalPriceLabel.setText(String.format("%.2f грн", total));
    }

    public void refreshData() {
        updateDataFromControllers();
    }


    /*   private void onSave() {
        System.out.println("Збереження букета: " + bouquetNameLabel.getText());
        try {
            // 1. Відкрити транзакцію
            connection.setAutoCommit(false);

            BouquetDAO bouquetDAO = new BouquetDAO(connection);
            FlowerInBouquetDAO fibDAO = new FlowerInBouquetDAO(connection);
            AccessoryDAO accessoryDAO = new AccessoryDAO(connection);

            // 2. Вставити букет у базу і отримати id
            String name = bouquetFormController.getBouquetName();
            if (name == null || name.isBlank()) {
                showAlert("Помилка", "Назва букета не може бути порожньою.");
                return;
            }
            Bouquet bouquet = new Bouquet();
            bouquet.setName(name);
            bouquetDAO.insert(bouquet);


            // 3. Вставити квіти з bouquet
            ObservableList<FlowerInBouquet> flowers = flowersController.getFlowersInBouquet();
            for (FlowerInBouquet fib : flowers) {
                fib.setBouquet(bouquet);  // зв'язати квітку з букетом
                fibDAO.insert(fib);
            }

            // 4. Вставити аксесуари
            ObservableList<Accessory> accessories = accessoriesController.getAllAccessories();
            for (Accessory acc : accessories) {
                acc.setBouquet(bouquet); // зв'язати аксесуар з букетом
                accessoryDAO.insert(acc);
                // Якщо аксесуар - Box, Ribbon, Paper, GreetingCard, треба додати відповідні підкласи
                if (acc instanceof Box) {
                    BoxDAO boxDAO = new BoxDAO(connection);
                    boxDAO.insert((Box) acc);
                    // Додайте insert для Box, якщо реалізовано
                }
                // Аналогічно для Ribbon, Paper, GreetingCard
            }

            // 5. Коміт транзакції
            connection.commit();

            showAlert("Успіх", "Букет успішно збережено у базу.");

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            showAlert("Помилка", "Не вдалося зберегти букет: " + e.getMessage());
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
*/
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void onPrint() {
        System.out.println("Друк букета: " + bouquetNameLabel.getText());
        // TODO: Реалізувати друк
    }


    private void onSave() {
        System.out.println("Збереження букета: " + bouquetNameLabel.getText());
        try {
            connection.setAutoCommit(false);

            BouquetDAO bouquetDAO = new BouquetDAO(connection);
            FlowerInBouquetDAO fibDAO = new FlowerInBouquetDAO(connection);
            AccessoryDAO accessoryDAO = new AccessoryDAO(connection);

            String name = bouquetFormController.getBouquetName();
            if (name == null || name.isBlank()) {
                showAlert("Помилка", "Назва букета не може бути порожньою.");
                return;
            }

            Bouquet bouquet = bouquetFormController.getCurrentBouquet();
            if (bouquet == null) {
                bouquet = new Bouquet();
                bouquet.setName(name);
                bouquetDAO.insert(bouquet);
            } else {
                bouquet.setName(name);
                bouquetDAO.update(bouquet); // потрібно реалізувати метод оновлення в DAO
                // Можливо, також видалити старі записи про квіти і аксесуари, щоб замінити на нові
                fibDAO.deleteByBouquetId(bouquet.getId());
                accessoryDAO.deleteByBouquetId(bouquet.getId());
            }

            // 3. Вставити квіти з bouquet
            ObservableList<FlowerInBouquet> flowers = flowersController.getFlowersInBouquet();
            for (FlowerInBouquet fib : flowers) {
                fib.setBouquet(bouquet);  // зв'язати квітку з букетом
                fibDAO.insert(fib);
            }

            // 4. Вставити аксесуари
            ObservableList<Accessory> accessories = accessoriesController.getAllAccessories();
            for (Accessory acc : accessories) {
                acc.setBouquet(bouquet); // зв'язати аксесуар з букетом
                accessoryDAO.insert(acc);
                // Якщо аксесуар - Box, Ribbon, Paper, GreetingCard, треба додати відповідні підкласи
                if (acc instanceof Box) {
                    BoxDAO boxDAO = new BoxDAO(connection);
                    boxDAO.insert((Box) acc);
                    // Додайте insert для Box, якщо реалізовано
                }
                // Аналогічно для Ribbon, Paper, GreetingCard
            }

            connection.commit();
            showAlert("Успіх", "Букет успішно збережено!");

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            showAlert("Помилка", "Не вдалося зберегти букет.");
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



}
