package com.example.kursova_flowers.util;

import com.example.kursova_flowers.model.AccessoryType;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TableViewHelper {

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

