// BouquetFormController.java
package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.db.DBManager;
import com.sun.jdi.connect.spi.Connection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import java.io.IOException;

public class BouquetFormController {

    @FXML
    private VBox leftPanel;

    @FXML
    private Button backButton;

    @FXML
    private TextField nameField;

    @FXML
    private Button flowersPageButton;

    @FXML
    private Button accessoriesPageButton;

    @FXML
    private Button summaryPageButton;

    @FXML
    private BorderPane rightPanel;
    java.sql.Connection connection; // маємо підключення до бази

    // Зберігаємо посилання на завантажені вікна та контролери
    private Node flowersSectionNode;
    private FlowersSectionController flowersSectionController;

    private Node accessoriesSectionNode;
    //private AccessoriesSectionController accessoriesSectionController;

    private Node summarySectionNode;
   // private SummarySectionController summarySectionController;


   @FXML
   public void initialize() {
       try {
          connection = DBManager.getConnection();

           // Попередньо завантажуємо всі сторінки та зберігаємо їх
           loadFlowersPage();
           loadAccessoriesPage();
           loadSummaryPage();

           // Встановлюємо початкову сторінку
           showPage(flowersSectionNode);

       } catch (IOException e) {
           e.printStackTrace();
       }
   }

    // Методи для перемикання сторінок (покищо пусті — реалізуємо потім)
    private void loadFlowersPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/kursova_flowers/app/flowers_section.fxml"));
        flowersSectionNode = loader.load();
        flowersSectionController = loader.getController();
        flowersSectionController.setConnection(connection);
    }

    private void loadAccessoriesPage() throws IOException {
      //  FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/kursova_flowers/app/accessories_section.fxml"));
      //  accessoriesSectionNode = loader.load();
     //   accessoriesSectionController = loader.getController();
     //   accessoriesSectionController.setConnection(connection);
    }

    private void loadSummaryPage() throws IOException {
       // FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/kursova_flowers/app/summary_section.fxml"));
     //   summarySectionNode = loader.load();
     //   summarySectionController = loader.getController();
      //  summarySectionController.setConnection(connection);
    }

    private void showPage(Node page) {
        rightPanel.setCenter(page);
    }

    @FXML
    private void showFlowersPage() {
        showPage(flowersSectionNode);
    }

    @FXML
    private void showAccessoriesPage() {
        showPage(accessoriesSectionNode);
    }

    @FXML
    private void showSummaryPage() {
        showPage(summarySectionNode);
    }

    @FXML
    private void goBack() {
        // TODO: реалізувати повернення на головну
    }
}
