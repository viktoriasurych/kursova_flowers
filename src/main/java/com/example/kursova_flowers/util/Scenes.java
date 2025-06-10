package com.example.kursova_flowers.util;

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
    // інші сцени...

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
