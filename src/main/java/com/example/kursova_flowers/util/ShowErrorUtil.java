package com.example.kursova_flowers.util;

import javafx.scene.control.Alert;

public class ShowErrorUtil {

    public static void showAlert(String title, String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
