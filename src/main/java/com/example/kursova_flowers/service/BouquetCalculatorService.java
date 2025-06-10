package com.example.kursova_flowers.service;

import com.example.kursova_flowers.model.*;

public class BouquetCalculatorService {

    /**
     * Розрахунок загальної ціни букета.
     * @param bouquet - букет з квітами та аксесуарами
     * @return загальна ціна букета
     */
    public double calculateTotalPrice(Bouquet bouquet) {
        if (bouquet == null) {
            return 0;
        }
        double total = 0;

        if (bouquet.getFlowers() != null) {
            for (FlowerInBouquet flowerInBouquet : bouquet.getFlowers()) {
                Flower flower = flowerInBouquet.getFlower();
                if (flower != null) {
                    total += flower.getPrice() * flowerInBouquet.getQuantity();
                }
            }
        }

        if (bouquet.getAccessories() != null) {
            for (Accessory accessory : bouquet.getAccessories()) {
                AccessoryType type = accessory.getType();
                if (type != null) {
                    total += type.getBasePrice();
                }
            }
        }

        return total;
    }
}
