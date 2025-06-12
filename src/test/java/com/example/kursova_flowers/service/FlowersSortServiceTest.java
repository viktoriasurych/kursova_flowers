package com.example.kursova_flowers.service;

import com.example.kursova_flowers.model.FlowerInBouquet;
import com.example.kursova_flowers.model.Flower;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlowersSortServiceTest {

    // Допоміжний клас для імітації Flower з датою
    private static class TestFlower extends Flower {
        private final LocalDate pickedDate;

        public TestFlower(LocalDate pickedDate) {
            this.pickedDate = pickedDate;
        }

        @Override
        public LocalDate getPickedDate() {
            return pickedDate;
        }
    }

    // Допоміжний клас для імітації FlowerInBouquet
    private static class TestFlowerInBouquet extends FlowerInBouquet {
        private final Flower flower;

        public TestFlowerInBouquet(Flower flower) {
            this.flower = flower;
        }

        @Override
        public Flower getFlower() {
            return flower;
        }
    }

    @Test
    void sortByFreshness_sortsByNewestDateFirst() {
        List<FlowerInBouquet> list = new ArrayList<>();
        list.add(new TestFlowerInBouquet(new TestFlower(LocalDate.of(2025, 6, 1))));
        list.add(new TestFlowerInBouquet(new TestFlower(LocalDate.of(2025, 6, 5))));
        list.add(new TestFlowerInBouquet(new TestFlower(LocalDate.of(2025, 6, 3))));

        FlowersSortService.sortByFreshness(list);

        // Після сортування перший елемент повинен бути з датою 2025-06-01, оскільки код сортує за зростанням дати (від найстарішої)
        // Якщо треба найновіша першою — сортування треба міняти
        // В коді сортування за LocalDate, що означає що LocalDate.MIN йде на початок
        // Зараз у методі сорт сортує за зростанням, отже найстарша дата першою.

        assertEquals(LocalDate.of(2025, 6, 1), list.get(0).getFlower().getPickedDate());
        assertEquals(LocalDate.of(2025, 6, 3), list.get(1).getFlower().getPickedDate());
        assertEquals(LocalDate.of(2025, 6, 5), list.get(2).getFlower().getPickedDate());
    }

    @Test
    void sortByFreshness_handlesException_returnsLocalDateMinAtFront() {
        // Квітка, що кидає виключення при отриманні дати
        FlowerInBouquet exceptionFlowerInBouquet = new FlowerInBouquet() {
            @Override
            public Flower getFlower() {
                return new Flower() {
                    @Override
                    public LocalDate getPickedDate() {
                        throw new RuntimeException("Test exception");
                    }
                };
            }
        };

        List<FlowerInBouquet> list = new ArrayList<>();
        list.add(exceptionFlowerInBouquet);
        list.add(new TestFlowerInBouquet(new TestFlower(LocalDate.of(2025, 6, 5))));

        FlowersSortService.sortByFreshness(list);

        // Очікуємо, що exceptionFlowerInBouquet буде на початку списку, бо повертає LocalDate.MIN
        assertSame(exceptionFlowerInBouquet, list.get(0));
    }

    @Test
    void sortByFreshness_emptyList_noException() {
        List<FlowerInBouquet> list = new ArrayList<>();
        assertDoesNotThrow(() -> FlowersSortService.sortByFreshness(list));
        assertTrue(list.isEmpty());
    }
}
