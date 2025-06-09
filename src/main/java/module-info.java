module com.example.kursova_flowers {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.desktop;
    requires jdk.jdi;
    requires layout;
    requires kernel;
    requires io;

    // opens com.example.kursova_flowers to javafx.fxml;
    //exports com.example.kursova_flowers;
    exports com.example.kursova_flowers.app;
    opens com.example.kursova_flowers.app to javafx.fxml;
    exports com.example.kursova_flowers.controller;
    opens com.example.kursova_flowers.controller to javafx.fxml;
}