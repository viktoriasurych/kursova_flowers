module com.example.kursova_flowers {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.example.kursova_flowers to javafx.fxml;
    exports com.example.kursova_flowers;
    exports com.example.kursova_flowers.app;
    opens com.example.kursova_flowers.app to javafx.fxml;
}