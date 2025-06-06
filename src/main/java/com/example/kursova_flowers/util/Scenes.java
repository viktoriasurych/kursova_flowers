package com.example.kursova_flowers.util;

public enum Scenes {
    MAIN("/com/example/kursova_flowers/app/main-view.fxml", "Cherrish"),
    FLOWER("/com/example/kursova_flowers/app/flower-view.fxml", "Квіти"),
    BOUQUET("/com/example/kursova_flowers/app/b-view.fxml", "Букети"),
    CREATE("/com/example/kursova_flowers/app/c-view.fxml", "Букет"),
    ACCESSORY("/com/example/kursova_flowers/app/accessory-view.fxml", "Аксесуари");
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
