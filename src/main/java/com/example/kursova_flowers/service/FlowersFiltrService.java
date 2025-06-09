package com.example.kursova_flowers.service;

import com.example.kursova_flowers.model.FlowerInBouquet;

import java.util.List;
import java.util.stream.Collectors;

public class FlowersFiltrService {
    /**
     * Фільтрує список квіток за діапазоном довжини стебла.
     * @param flowers список квіток
     * @param minLength мінімальна довжина (включно)
     * @param maxLength максимальна довжина (включно)
     * @return відфільтрований список
     */
    public List<FlowerInBouquet> filterByStemLengthRange(List<FlowerInBouquet> flowers, Double minLength, Double maxLength) {
        return flowers.stream()
                .filter(fib -> {
                    double length = fib.getStemLength();
                    boolean greaterOrEqualMin = (minLength == null) || (length >= minLength);
                    boolean lessOrEqualMax = (maxLength == null) || (length <= maxLength);
                    return greaterOrEqualMin && lessOrEqualMax;
                })
                .collect(Collectors.toList());
    }
}
