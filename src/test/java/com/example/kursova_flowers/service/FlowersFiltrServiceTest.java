package com.example.kursova_flowers.service;

import com.example.kursova_flowers.model.Flower;
import com.example.kursova_flowers.model.FlowerInBouquet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlowersFiltrServiceTest {

    private final FlowersFiltrService filtrService = new FlowersFiltrService();

    private FlowerInBouquet createFlowerInBouquet(double stemLength) {
        Flower flower = new Flower();
        flower.setName("Test");
        flower.setPrice(5.0);

        FlowerInBouquet fib = new FlowerInBouquet();
        fib.setFlower(flower);
        fib.setQuantity(1);
        fib.setStemLength(stemLength);
        return fib;
    }

    @Test
    void testFilterAllInRange() {
        List<FlowerInBouquet> flowers = List.of(
                createFlowerInBouquet(10),
                createFlowerInBouquet(15),
                createFlowerInBouquet(20)
        );

        List<FlowerInBouquet> result = filtrService.filterByStemLengthRange(flowers, 10.0, 20.0);
        assertEquals(3, result.size());
    }

    @Test
    void testFilterSomeInRange() {
        List<FlowerInBouquet> flowers = List.of(
                createFlowerInBouquet(8),
                createFlowerInBouquet(12),
                createFlowerInBouquet(25)
        );

        List<FlowerInBouquet> result = filtrService.filterByStemLengthRange(flowers, 10.0, 20.0);
        assertEquals(1, result.size());
        assertEquals(12.0, result.get(0).getStemLength());
    }

    @Test
    void testFilterNoneInRange() {
        List<FlowerInBouquet> flowers = List.of(
                createFlowerInBouquet(5),
                createFlowerInBouquet(25)
        );

        List<FlowerInBouquet> result = filtrService.filterByStemLengthRange(flowers, 10.0, 20.0);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFilterWithNullMin() {
        List<FlowerInBouquet> flowers = List.of(
                createFlowerInBouquet(5),
                createFlowerInBouquet(15),
                createFlowerInBouquet(25)
        );

        List<FlowerInBouquet> result = filtrService.filterByStemLengthRange(flowers, null, 15.0);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(f -> f.getStemLength() <= 15.0));
    }

    @Test
    void testFilterWithNullMax() {
        List<FlowerInBouquet> flowers = List.of(
                createFlowerInBouquet(5),
                createFlowerInBouquet(15),
                createFlowerInBouquet(25)
        );

        List<FlowerInBouquet> result = filtrService.filterByStemLengthRange(flowers, 10.0, null);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(f -> f.getStemLength() >= 10.0));
    }

    @Test
    void testFilterWithNullMinAndMax() {
        List<FlowerInBouquet> flowers = List.of(
                createFlowerInBouquet(7),
                createFlowerInBouquet(14)
        );

        List<FlowerInBouquet> result = filtrService.filterByStemLengthRange(flowers, null, null);
        assertEquals(2, result.size());
    }

    @Test
    void testFilterEmptyList() {
        List<FlowerInBouquet> result = filtrService.filterByStemLengthRange(List.of(), 10.0, 20.0);
        assertTrue(result.isEmpty());
    }
}
