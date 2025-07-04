package com.example.kursova_flowers.controller;

import com.example.kursova_flowers.model.Bouquet;
import com.example.kursova_flowers.util.ImageUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.logging.Logger;

public class BouquetCardController {

    private static final Logger LOGGER = Logger.getLogger(BouquetCardController.class.getName());

    @FXML private ImageView imageView;
    @FXML private Label nameLabel;
    @FXML private Label priceLabel;
    @FXML private Button infoButton;
    @FXML private Button deleteButton;

    private Bouquet bouquet;
    private Runnable onEdit;
    private Runnable onDeleteCallback;

    public void setData(Bouquet bouquet, double totalPrice, Runnable onEdit, Runnable onDelete) {
        LOGGER.info("Встановлення даних для картки букета: " + bouquet.getName());
        this.bouquet = bouquet;
        this.onEdit = onEdit;
        this.onDeleteCallback = onDelete;

        nameLabel.setText(bouquet.getName());
        priceLabel.setText("Ціна: " + totalPrice + " грн");

        setRandomImage();
    }

    private void setRandomImage(){Image randomImage = ImageUtil.getRandomBouquetImage();
        if (randomImage != null) {
            imageView.setImage(randomImage);
        }}

    @FXML
    private void onInfo() {
        if (onEdit != null)  onEdit.run();
    }

    @FXML
    private void onDelete() {
        if (onDeleteCallback != null) onDeleteCallback.run();
    }
}

