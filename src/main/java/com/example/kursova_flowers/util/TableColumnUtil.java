package com.example.kursova_flowers.util;

import com.example.kursova_flowers.controller.DatePickerTableCell;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TableColumnUtil {

    public static <T> void makeEditableStringColumn(TableColumn<T, String> column,
                                                    Function<T, String> getter,
                                                    BiConsumer<T, String> setter) {
        column.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(e -> {
            setter.accept(e.getRowValue(), e.getNewValue());
        });
    }

    public static <T> void makeEditableDoubleColumn(TableColumn<T, Double> column,
                                                    Function<T, Double> getter,
                                                    BiConsumer<T, Double> setter) {
        column.setCellValueFactory(data -> new SimpleDoubleProperty(getter.apply(data.getValue())).asObject());
        column.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        column.setOnEditCommit(e -> {
            setter.accept(e.getRowValue(), e.getNewValue());
        });
    }

    public static <T> void makeEditableIntegerColumn(TableColumn<T, Integer> column,
                                                     Function<T, Integer> getter,
                                                     BiConsumer<T, Integer> setter) {
        column.setCellValueFactory(data -> new SimpleIntegerProperty(getter.apply(data.getValue())).asObject());
        column.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        column.setOnEditCommit(e -> {
            setter.accept(e.getRowValue(), e.getNewValue());
        });
    }

    public static <T> void makeEditableDateColumn(TableColumn<T, java.time.LocalDate> column,
                                                  Function<T, java.time.LocalDate> getter,
                                                  BiConsumer<T, java.time.LocalDate> setter) {
        column.setCellValueFactory(data -> new SimpleObjectProperty<>(getter.apply(data.getValue())));
        column.setCellFactory(col -> new DatePickerTableCell<>());
        column.setOnEditCommit(e -> {
            setter.accept(e.getRowValue(), e.getNewValue());
        });
    }
    public static <T> void makeReadOnlyStringColumn(TableColumn<T, String> column,
                                                    Function<T, String> getter) {
        column.setCellValueFactory(data -> new SimpleStringProperty(getter.apply(data.getValue())));
        column.setEditable(false);
    }
    public static <T> void makeReadOnlyDoubleColumn(TableColumn<T, Double> column,
                                                    Function<T, Double> getter) {
        column.setCellValueFactory(data -> new SimpleDoubleProperty(getter.apply(data.getValue())).asObject());
        column.setEditable(false);
    }

    public static <T> void makeReadOnlyIntegerColumn(TableColumn<T, Integer> column,
                                                     Function<T, Integer> getter) {
        column.setCellValueFactory(data -> new SimpleIntegerProperty(getter.apply(data.getValue())).asObject());
        column.setEditable(false);
    }

    public static <T> void makeSaveButtonColumn(TableColumn<T, Void> column,
                                                BiConsumer<T, Integer> saveAction) {
        column.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("✔");

            {
                btn.setOnAction(e -> {
                    T item = getTableView().getItems().get(getIndex());
                    saveAction.accept(item, getIndex());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }
}
