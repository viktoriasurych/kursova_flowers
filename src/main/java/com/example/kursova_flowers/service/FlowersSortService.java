package com.example.kursova_flowers.service;

import com.example.kursova_flowers.model.FlowerInBouquet;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class FlowersSortService {

    /**
     * Сортує список квітів у букеті за свіжістю (датою зрізу).
     * Чим новіша квітка — тим раніше вона з'являється у списку.
     *
     * @param list список об'єктів {@link FlowerInBouquet}, який потрібно відсортувати
     */
    public static void sortByFreshness(List<FlowerInBouquet> list) {
        list.sort(Comparator.comparing(f -> {
            try {
                return f.getFlower().getPickedDate();
            } catch (Exception e) {
                return LocalDate.MIN; // некоректні дати — в кінець списку
            }
        }));
    }
}
