package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.dao.*;
import com.example.kursova_flowers.model.*;
import com.example.kursova_flowers.service.*;
import com.example.kursova_flowers.util.*;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class ReceiptSectionController {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptSectionController.class);

    @FXML private VBox flowersSection;
    @FXML private VBox cardsSection;
    @FXML private VBox boxesSection;
    @FXML private VBox ribbonsSection;
    @FXML private VBox papersSection;

    @FXML private Label bouquetNameLabel;

    @FXML private TableView<FlowerInBouquet> flowersTable;
    @FXML private TableColumn<FlowerInBouquet, String> flowerTypeColumn;
    @FXML private TableColumn<FlowerInBouquet, String> flowerNameColumn;
    @FXML private TableColumn<FlowerInBouquet, Double> flowerStemLengthColumn;
    @FXML private TableColumn<FlowerInBouquet, Integer> flowerQuantityColumn;
    @FXML private TableColumn<FlowerInBouquet, Double> flowerTotalPriceColumn;

    @FXML private TableView<GreetingCard> cardsTable;
    @FXML private TableColumn<GreetingCard, String> cardTextColumn;
    @FXML private TableColumn<GreetingCard, String> cardColorColumn;
    @FXML private TableColumn<GreetingCard, Double> cardPriceColumn;

    @FXML private TableView<Box> boxesTable;
    @FXML private TableColumn<Box, String> boxTypeColumn;
    @FXML private TableColumn<Box, String> boxColorColumn;
    @FXML private TableColumn<Box, Double> boxPriceColumn;

    @FXML private TableView<Ribbon> ribbonsTable;
    @FXML private TableColumn<Ribbon, Double> ribbonWidthColumn;
    @FXML private TableColumn<Ribbon, String> ribbonColorColumn;
    @FXML private TableColumn<Ribbon, Double> ribbonPriceColumn;

    @FXML private TableView<Paper> papersTable;
    @FXML private TableColumn<Paper, String> paperMaterialColumn;
    @FXML private TableColumn<Paper, String> paperColorColumn;
    @FXML private TableColumn<Paper, Double> paperPriceColumn;

    @FXML private Label totalPriceLabel;

    @FXML private Button saveButton;
    @FXML private Button printButton;

    private FlowersSectionController flowersController;
    private AccessoriesSectionController accessoriesController;
    private BouquetFormController bouquetFormController;

    private BouquetCalculatorService calculatorService = new BouquetCalculatorService();
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;

    }

    /**
     * Прив'язує текстове поле з назвою букета до мітки.
     *
     * @param nameField текстове поле з назвою
     */
    public void bindBouquetName(TextField nameField) {
        bouquetNameLabel.textProperty().bind(nameField.textProperty());
    }

    @FXML
    public void initialize() {
        setColumn();
    }

    public void setColumn() {

        TableColumnUtil.makeReadOnlyStringColumn(flowerTypeColumn, item -> item.getFlower().getType().getName());
        TableColumnUtil.makeReadOnlyStringColumn(flowerNameColumn, item -> item.getFlower().getName());
        TableColumnUtil.makeReadOnlyDoubleColumn(flowerStemLengthColumn, FlowerInBouquet::getStemLength);
        TableColumnUtil.makeReadOnlyIntegerColumn(flowerQuantityColumn, FlowerInBouquet::getQuantity);
        TableColumnUtil.makeReadOnlyDoubleColumn(flowerTotalPriceColumn,
                item -> item.getQuantity() * item.getFlower().getPrice());

        TableColumnUtil.makeReadOnlyStringColumn(cardTextColumn, GreetingCard::getText);
        TableColumnUtil.makeReadOnlyStringColumn(cardColorColumn, GreetingCard::getColor);
        TableColumnUtil.makeReadOnlyDoubleColumn(cardPriceColumn, item -> item.getType().getBasePrice());

        TableColumnUtil.makeReadOnlyStringColumn(boxTypeColumn, Box::getBoxType);
        TableColumnUtil.makeReadOnlyStringColumn(boxColorColumn, Box::getColor);
        TableColumnUtil.makeReadOnlyDoubleColumn(boxPriceColumn, item -> item.getType().getBasePrice());

        TableColumnUtil.makeReadOnlyDoubleColumn(ribbonWidthColumn, Ribbon::getWidth);
        TableColumnUtil.makeReadOnlyStringColumn(ribbonColorColumn, Ribbon::getColor);
        TableColumnUtil.makeReadOnlyDoubleColumn(ribbonPriceColumn, item -> item.getType().getBasePrice());

        TableColumnUtil.makeReadOnlyStringColumn(paperMaterialColumn, Paper::getMaterial);
        TableColumnUtil.makeReadOnlyStringColumn(paperColorColumn, Paper::getColor);
        TableColumnUtil.makeReadOnlyDoubleColumn(paperPriceColumn, item -> item.getType().getBasePrice());

        saveButton.setOnAction(event -> onSave());
        printButton.setOnAction(event -> onPrint());
    }

    /**
     * Встановлює зовнішні контролери, необхідні для завантаження даних у секцію квитанції.
     *
     * @param flowersController контролер квітів
     * @param accessoriesController контролер аксесуарів
     * @param bouquetFormController контролер форми букета
     */
    public void setControllers(FlowersSectionController flowersController,
                               AccessoriesSectionController accessoriesController,
                               BouquetFormController bouquetFormController) {
        this.flowersController = flowersController;
        this.accessoriesController = accessoriesController;
        this.bouquetFormController = bouquetFormController;


        updateDataFromControllers();
    }

    /**
     * Прив'язує висоту таблиці до кількості її рядків.
     *
     * @param table таблиця, яку потрібно адаптувати
     */
    private void bindTableHeight(TableView<?> table) {
        table.setFixedCellSize(25);
        table.prefHeightProperty().bind(
                Bindings.size(table.getItems())
                        .multiply(table.getFixedCellSize())
                        .add(30)
        );
    }

    /**
     * Оновлює таблиці та секції інтерфейсу на основі даних із зовнішніх контролерів.
     * Встановлює видимість секцій, оновлює загальну вартість букета.
     */
    private void updateDataFromControllers() {
        if (flowersController == null || accessoriesController == null || bouquetFormController == null) {
            logger.warn("Один або кілька контролерів не встановлено, пропуск оновлення даних");
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
        logger.debug("Завантажено {} квітів у таблицю", flowers.size());

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
        logger.info("Оновлення даних завершено. Загальна ціна букета: {} грн", total);
    }

    /**
     * Оновлює дані у всіх таблицях та секціях на основі актуального стану контролерів квітів та аксесуарів.
     * Перераховує загальну ціну букета.
     */
    public void refreshData() {
        updateDataFromControllers();
    }

    private void onPrint() {
        System.out.println("Друк букета: " + bouquetNameLabel.getText());
        try {
            Bouquet bouquet = bouquetFormController.getCurrentBouquet();
            if (bouquet == null) {
                ShowErrorUtil.showAlert("Помилка", "Немає букета для друку.", Alert.AlertType.ERROR);
                return;
            }

            String filePath = "receipt_" + bouquet.getName().replaceAll("\\s+", "_") + ".pdf";
            ReceiptPdfService pdfService = new ReceiptPdfService();
            pdfService.generatePdfReceipt(bouquet, filePath);
            logger.info("Друк букета завершено успішно");
            ShowErrorUtil.showAlert("Успіх", "Чек збережено у файлі: " + filePath, Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            ShowErrorUtil.showAlert("Помилка", "Не вдалося створити PDF.", Alert.AlertType.ERROR);
            logger.error("Помилка під час друку букета", e);
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
                ShowErrorUtil.showAlert("Помилка", "Назва букета не може бути порожньою.", Alert.AlertType.ERROR);
                return;
            }

            Bouquet bouquet = bouquetFormController.getCurrentBouquet();
            if (bouquet == null) {
                bouquet = new Bouquet();
                bouquet.setName(name);
                bouquetDAO.insert(bouquet);
            } else {
                bouquet.setName(name);
                bouquetDAO.update(bouquet);
                fibDAO.deleteByBouquetId(bouquet.getId());
                accessoryDAO.deleteByBouquetId(bouquet.getId());
            }

            ObservableList<FlowerInBouquet> flowers = flowersController.getFlowersInBouquet();
            for (FlowerInBouquet fib : flowers) {
                fib.setBouquet(bouquet);
                fibDAO.insert(fib);
            }

            ObservableList<Accessory> accessories = accessoriesController.getAllAccessories();
            for (Accessory acc : accessories) {
                acc.setBouquet(bouquet);
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


            }

            connection.commit();
            ShowErrorUtil.showAlert("Успіх", "Букет успішно збережено!", Alert.AlertType.INFORMATION);

            logger.info("Букет успішно збережено");
            SceneUtil.openSceneFromButton(saveButton, Scenes.BOUQUET);


        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            logger.error("Помилка під час збереження букета", e);
            ShowErrorUtil.showAlert("Помилка", "Не вдалося зберегти букет.", Alert.AlertType.ERROR);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
