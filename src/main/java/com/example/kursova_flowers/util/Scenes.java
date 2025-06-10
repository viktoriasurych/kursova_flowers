package com.example.kursova_flowers.util;

/**
 * Перелічення {@code Scenes} зберігає FXML-шляхи до вікон застосунку
 * та їхні відповідні заголовки. Використовується для перемикання між сценами.
 */
public enum Scenes {
    MAIN("/com/example/kursova_flowers/app/main-view.fxml", "Cherrish"),
    FLOWER("/com/example/kursova_flowers/app/flower-view.fxml", "Квіти"),
    BOUQUET("/com/example/kursova_flowers/app/bouquets-view.fxml", "Букети"),
    CREATE("/com/example/kursova_flowers/app/bouquetcreate-view.fxml", "Букет"),
    ACCESSORY("/com/example/kursova_flowers/app/accessory-view.fxml", "Аксесуари"),

    CARDBOUQUET("/com/example/kursova_flowers/app/bouquet-card.fxml", "картки букетів"),

    SECTIONFLOWER("/com/example/kursova_flowers/app/flowers_section.fxml", "Секція квітів"),
    SECTIONACCESSORY("/com/example/kursova_flowers/app/accessory_section.fxml", "Секція аксесуарів"),
    SECTIONRECEIPT("/com/example/kursova_flowers/app/receipt-section.fxml", "Секція чеку");

    private final String fxmlPath;
    private final String title;

    Scenes(String fxmlPath, String title) {
        this.fxmlPath = fxmlPath;
        this.title = title;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public String getTitle() {
        return title;
    }
    }
