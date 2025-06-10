package com.example.kursova_flowers.controller;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

import java.time.LocalDate;

public class DatePickerTableCell<S> extends TableCell<S, LocalDate> {

    private final DatePicker datePicker = new DatePicker();

    public DatePickerTableCell() {
        datePicker.setEditable(false);

        datePicker.setOnAction(e -> {
            if (datePicker.getValue() != null) {
                commitEdit(datePicker.getValue());
            }
        });

        // ENTER або ESC
        datePicker.getEditor().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                commitEdit(datePicker.getValue());
            } else if (event.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });

        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    protected void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else if (isEditing()) {
            datePicker.setValue(item);
            setGraphic(datePicker);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } else {
            setText(item.toString());
            setGraphic(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (getItem() != null) {
            datePicker.setValue(getItem());
            setGraphic(datePicker);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            datePicker.show();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem() != null ? getItem().toString() : "");
        setGraphic(null);
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void commitEdit(LocalDate newValue) {
        super.commitEdit(newValue);
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    /**
     * Фабрика для використання в TableColumn
     */
    public static <S> Callback<TableColumn<S, LocalDate>, TableCell<S, LocalDate>> forTableColumn() {
        return column -> new DatePickerTableCell<>();
    }
}
