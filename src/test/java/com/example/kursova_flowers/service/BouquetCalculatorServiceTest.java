package com.example.kursova_flowers.service;

import com.example.kursova_flowers.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BouquetCalculatorServiceTest {

    private final BouquetCalculatorService calculator = new BouquetCalculatorService();

    @Test
    void testCalculateTotalPriceWithFlowersAndAccessories() {
        Flower rose = new Flower();
        rose.setName("Rose");
        rose.setPrice(10.0);

        Flower tulip = new Flower();
        tulip.setName("Tulip");
        tulip.setPrice(5.0);

        FlowerInBouquet roseItem = new FlowerInBouquet();
        roseItem.setFlower(rose);
        roseItem.setQuantity(3); // 3 * 10 = 30

        FlowerInBouquet tulipItem = new FlowerInBouquet();
        tulipItem.setFlower(tulip);
        tulipItem.setQuantity(4); // 4 * 5 = 20

        AccessoryType ribbonType = new AccessoryType();
        ribbonType.setBasePrice(7.0);

        Ribbon ribbon = new Ribbon();
        ribbon.setType(ribbonType);

        AccessoryType cardType = new AccessoryType();
        cardType.setBasePrice(3.0);

        GreetingCard card = new GreetingCard();
        card.setType(cardType);

        Bouquet bouquet = new Bouquet();
        bouquet.setFlowers(List.of(roseItem, tulipItem));
        bouquet.setAccessories(List.of(ribbon, card));

        double total = calculator.calculateTotalPrice(bouquet);
        assertEquals(60.0, total); // 30 + 20 + 7 + 3
    }

    @Test
    void testCalculateTotalPriceWithNullBouquet() {
        double total = calculator.calculateTotalPrice(null);
        assertEquals(0.0, total);
    }

    @Test
    void testCalculateTotalPriceWithNoFlowersOrAccessories() {
        Bouquet bouquet = new Bouquet();
        bouquet.setFlowers(null);
        bouquet.setAccessories(null);
        double total = calculator.calculateTotalPrice(bouquet);
        assertEquals(0.0, total);
    }

    @Test
    void testCalculateTotalPriceWithEmptyLists() {
        Bouquet bouquet = new Bouquet();
        bouquet.setFlowers(List.of());
        bouquet.setAccessories(List.of());
        double total = calculator.calculateTotalPrice(bouquet);
        assertEquals(0.0, total);
    }

    @Test
    void testCalculateTotalPriceIgnoresNullFlowersAndAccessories() {
        FlowerInBouquet flowerInBouquet = new FlowerInBouquet();
        flowerInBouquet.setFlower(null); // should be ignored
        flowerInBouquet.setQuantity(2);

        Accessory accessory = new Accessory() {}; // anonymous subclass
        accessory.setType(null); // should be ignored

        Bouquet bouquet = new Bouquet();
        bouquet.setFlowers(List.of(flowerInBouquet));
        bouquet.setAccessories(List.of(accessory));

        double total = calculator.calculateTotalPrice(bouquet);
        assertEquals(0.0, total);
    }
}
