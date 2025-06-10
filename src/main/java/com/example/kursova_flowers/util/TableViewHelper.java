package com.example.kursova_flowers.util;

import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.util.function.Function;

public class TableViewHelper {


    /**
     * Клас для опису конфігурації одного стовпця таблиці:
     * - стовпець {@link TableColumn}
     * - геттер для отримання значення
     * - тип значення (String, Integer, Double)
     */
    public static class ColumnConfig<T, V> {
        public final TableColumn<T, V> column;
        public final Function<T, V> getter;
        public final Class<V> valueType;

        public ColumnConfig(TableColumn<T, V> column, Function<T, V> getter, Class<V> valueType) {
            this.column = column;
            this.getter = getter;
            this.valueType = valueType;
        }
    }

    /**
     * Налаштовує {@link TableView} як таблицю лише для читання
     * на основі переданих конфігурацій стовпців.
     *
     * @param table   таблиця для налаштування
     * @param items   список елементів, які відображаються в таблиці
     * @param columns масив конфігурацій стовпців
     * @param <T>     тип елементів у таблиці
     * @param <V>     тип значення у стовпці
     */
    public static <T, V> void setupReadOnlyTable(TableView<T> table,
                                                 ObservableList<T> items,
                                                 ColumnConfig<T, V>[] columns) {
        table.setItems(items);
        for (ColumnConfig<T, V> config : columns) {
            if (config.valueType == String.class) {
                TableColumnUtil.makeReadOnlyStringColumn((TableColumn<T, String>) config.column,
                        (Function<T, String>) config.getter);
            } else if (config.valueType == Double.class) {
                TableColumnUtil.makeReadOnlyDoubleColumn((TableColumn<T, Double>) config.column,
                        (Function<T, Double>) config.getter);
            } else if (config.valueType == Integer.class) {
                TableColumnUtil.makeReadOnlyIntegerColumn((TableColumn<T, Integer>) config.column,
                        (Function<T, Integer>) config.getter);
            } else {
                throw new IllegalArgumentException("Unsupported type for read-only column: " + config.valueType);
            }
        }
    }


    }

