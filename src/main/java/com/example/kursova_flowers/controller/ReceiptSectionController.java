package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.dao.*;
import com.example.kursova_flowers.model.*;
import com.example.kursova_flowers.service.BouquetCalculatorService;
import com.example.kursova_flowers.service.ReceiptPdfService;
import com.example.kursova_flowers.util.*;
import javafx.beans.binding.Bindings;
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
        // Квіти
        TableColumnUtil.makeReadOnlyStringColumn(flowerTypeColumn, item -> item.getFlower().getType().getName());
        TableColumnUtil.makeReadOnlyStringColumn(flowerNameColumn, item -> item.getFlower().getName());
        TableColumnUtil.makeReadOnlyDoubleColumn(flowerStemLengthColumn, FlowerInBouquet::getStemLength);
        TableColumnUtil.makeReadOnlyIntegerColumn(flowerQuantityColumn, FlowerInBouquet::getQuantity);
        TableColumnUtil.makeReadOnlyDoubleColumn(flowerTotalPriceColumn,
                item -> item.getQuantity() * item.getFlower().getPrice());

        // Листівки
        TableColumnUtil.makeReadOnlyStringColumn(cardTextColumn, GreetingCard::getText);
        TableColumnUtil.makeReadOnlyStringColumn(cardColorColumn, GreetingCard::getColor);
        TableColumnUtil.makeReadOnlyDoubleColumn(cardPriceColumn, item -> item.getType().getBasePrice());

        // Коробки
        TableColumnUtil.makeReadOnlyStringColumn(boxTypeColumn, Box::getBoxType);
        TableColumnUtil.makeReadOnlyStringColumn(boxColorColumn, Box::getColor);
        TableColumnUtil.makeReadOnlyDoubleColumn(boxPriceColumn, item -> item.getType().getBasePrice());

        // Стрічки
        TableColumnUtil.makeReadOnlyDoubleColumn(ribbonWidthColumn, Ribbon::getWidth);
        TableColumnUtil.makeReadOnlyStringColumn(ribbonColorColumn, Ribbon::getColor);
        TableColumnUtil.makeReadOnlyDoubleColumn(ribbonPriceColumn, item -> item.getType().getBasePrice());

        // Папір
        TableColumnUtil.makeReadOnlyStringColumn(paperMaterialColumn, Paper::getMaterial);
        TableColumnUtil.makeReadOnlyStringColumn(paperColorColumn, Paper::getColor);
        TableColumnUtil.makeReadOnlyDoubleColumn(paperPriceColumn, item -> item.getType().getBasePrice());

        // Кнопки
        saveButton.setOnAction(event -> onSave());
        printButton.setOnAction(event -> onPrint());
    }


    /*   @FXML
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
    }*/

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
   /* private void updateDataFromControllers() {
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
    }*/

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
        flowersSection.setVisible(!flowers.isEmpty());
        flowersSection.setManaged(!flowers.isEmpty());

        TableViewHelper.setupReadOnlyTable(flowersTable, flowers, new TableViewHelper.ColumnConfig[]{
                new TableViewHelper.ColumnConfig<>(flowerTypeColumn,
                        flower -> flower.getFlower().getType().getName(), String.class),
                new TableViewHelper.ColumnConfig<>(flowerNameColumn,
                        flower -> flower.getFlower().getName(), String.class),
                new TableViewHelper.ColumnConfig<>(flowerStemLengthColumn,
                        FlowerInBouquet::getStemLength, Double.class),
                new TableViewHelper.ColumnConfig<>(flowerQuantityColumn,
                        FlowerInBouquet::getQuantity, Integer.class),
                new TableViewHelper.ColumnConfig<>(flowerTotalPriceColumn,
                        flower -> flower.getQuantity() * flower.getFlower().getPrice(), Double.class)
        });

        // Листівки
        ObservableList<GreetingCard> cards = accessoriesController.getGreetingCards();
        cardsSection.setVisible(!cards.isEmpty());
        cardsSection.setManaged(!cards.isEmpty());

        TableViewHelper.setupReadOnlyTable(cardsTable, cards, new TableViewHelper.ColumnConfig[]{
                new TableViewHelper.ColumnConfig<>(cardTextColumn, GreetingCard::getText, String.class),
                new TableViewHelper.ColumnConfig<>(cardColorColumn, GreetingCard::getColor, String.class),
                new TableViewHelper.ColumnConfig<>(cardPriceColumn,
                        card -> card.getType().getBasePrice(), Double.class)
        });

        // Коробки
        ObservableList<Box> boxes = accessoriesController.getBoxes();
        boxesSection.setVisible(!boxes.isEmpty());
        boxesSection.setManaged(!boxes.isEmpty());

        TableViewHelper.setupReadOnlyTable(boxesTable, boxes, new TableViewHelper.ColumnConfig[]{
                new TableViewHelper.ColumnConfig<>(boxTypeColumn, Box::getBoxType, String.class),
                new TableViewHelper.ColumnConfig<>(boxColorColumn, Box::getColor, String.class),
                new TableViewHelper.ColumnConfig<>(boxPriceColumn,
                        box -> box.getType().getBasePrice(), Double.class)
        });

        // Стрічки
        ObservableList<Ribbon> ribbons = accessoriesController.getRibbons();
        ribbonsSection.setVisible(!ribbons.isEmpty());
        ribbonsSection.setManaged(!ribbons.isEmpty());

        TableViewHelper.setupReadOnlyTable(ribbonsTable, ribbons, new TableViewHelper.ColumnConfig[]{
                new TableViewHelper.ColumnConfig<>(ribbonWidthColumn, Ribbon::getWidth, Double.class),
                new TableViewHelper.ColumnConfig<>(ribbonColorColumn, Ribbon::getColor, String.class),
                new TableViewHelper.ColumnConfig<>(ribbonPriceColumn,
                        ribbon -> ribbon.getType().getBasePrice(), Double.class)
        });

        // Папір
        ObservableList<Paper> papers = accessoriesController.getPapers();
        papersSection.setVisible(!papers.isEmpty());
        papersSection.setManaged(!papers.isEmpty());

        TableViewHelper.setupReadOnlyTable(papersTable, papers, new TableViewHelper.ColumnConfig[]{
                new TableViewHelper.ColumnConfig<>(paperMaterialColumn, Paper::getMaterial, String.class),
                new TableViewHelper.ColumnConfig<>(paperColorColumn, Paper::getColor, String.class),
                new TableViewHelper.ColumnConfig<>(paperPriceColumn,
                        paper -> paper.getType().getBasePrice(), Double.class)
        });

        // Загальна ціна
        Bouquet bouquet = new Bouquet();
        bouquet.setFlowers(flowers);
        bouquet.setAccessories(accessoriesController.getAllAccessories());

        double total = calculatorService.calculateTotalPrice(bouquet);
        totalPriceLabel.setText(String.format("%.2f грн", total));
    }


    public void refreshData() {
        updateDataFromControllers();
    }



    private void onPrint() {
        System.out.println("Друк букета: " + bouquetNameLabel.getText());
        try {
            // Отримати букет з форми
            Bouquet bouquet = bouquetFormController.getCurrentBouquet();
            if (bouquet == null) {
                ShowErrorUtil.showError("Помилка", "Немає букета для друку.");
                return;
            }

            String filePath = "receipt_" + bouquet.getName().replaceAll("\\s+", "_") + ".pdf";
            ReceiptPdfService pdfService = new ReceiptPdfService();
            pdfService.generatePdfReceipt(bouquet, filePath);

            ShowErrorUtil.showError("Успіх", "Чек збережено у файлі: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
            ShowErrorUtil.showError("Помилка", "Не вдалося створити PDF.");
        }
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
                ShowErrorUtil.showError("Помилка", "Назва букета не може бути порожньою.");
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

                switch (acc.getType().getId()){
                    case 1:
                        CardDAO cardDAO = new CardDAO(connection);
                        cardDAO.insert((GreetingCard) acc);break;
                    case 2:
                        BoxDAO boxDAO = new BoxDAO(connection);
                        boxDAO.insert((Box) acc);
                        break;
                    case 3:
                        RibbonDAO ribbonDAO = new RibbonDAO(connection);
                        ribbonDAO.insert((Ribbon) acc);
                        break;
                    case 4:
                        PaperDAO paperDAO = new PaperDAO(connection);
                        paperDAO.insert((Paper) acc);
                        break;
                }

                // Якщо аксесуар - Box, Ribbon, Paper, GreetingCard, треба додати відповідні підкласи
                /*if (acc instanceof Box) {
                    BoxDAO boxDAO = new BoxDAO(connection);
                    boxDAO.insert((Box) acc);
                }*/
                // Аналогічно для Ribbon, Paper, GreetingCard
            }

            connection.commit();
            ShowErrorUtil.showError("Успіх", "Букет успішно збережено!");

            SceneUtil.openSceneFromButton(saveButton, Scenes.BOUQUET);


        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            ShowErrorUtil.showError("Помилка", "Не вдалося зберегти букет.");
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



}
