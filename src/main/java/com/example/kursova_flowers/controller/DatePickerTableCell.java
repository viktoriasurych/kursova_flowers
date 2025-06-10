package com.example.kursova_flowers.controller;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

import java.time.LocalDate;

/**
 * Клітинка таблиці з DatePicker для вибору дати у TableView.
 *
 * @param <S> тип об’єкта рядка таблиці
 */
public class DatePickerTableCell<S> extends TableCell<S, LocalDate> {

    private final DatePicker datePicker = new DatePicker();

    /**
     * Конструктор, що налаштовує DatePicker і події для редагування дати.
     */
    public DatePickerTableCell() {
        datePicker.setEditable(false);

        datePicker.setOnAction(e -> {
            if (datePicker.getValue() != null) {
                commitEdit(datePicker.getValue());
            }
        });

        datePicker.getEditor().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                commitEdit(datePicker.getValue());
            } else if (event.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });

        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    /**
     * Оновлює відображення клітинки таблиці залежно від стану.
     *
     * @param item дата для відображення
     * @param empty чи є клітинка пустою
     */
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

    /**
     * Починає режим редагування клітинки.
     * Встановлює дату в DatePicker і показує його.
     */
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

    /**
     * Скасовує режим редагування.
     * Повертає відображення у вигляді тексту.
     */
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem() != null ? getItem().toString() : "");
        setGraphic(null);
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    /**
     * Завершує редагування і комітить нове значення.
     *
     * @param newValue нова дата, обрана користувачем
     */
    @Override
    public void commitEdit(LocalDate newValue) {
        super.commitEdit(newValue);
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

}
