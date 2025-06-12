package com.example.kursova_flowers.service;

import com.example.kursova_flowers.model.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReceiptPdfServiceTest {

    private ReceiptPdfService receiptPdfService;

    @BeforeEach
    void setUp() {
        receiptPdfService = new ReceiptPdfService();
    }

    @Test
    void testGeneratePdfReceipt_createsFile() throws IOException {
        // Підготовка даних для букета
        FlowerType flowerType = new FlowerType();
        flowerType.setName("Троянда");

        Flower flower = new Flower();
        flower.setName("Червона троянда");
        flower.setPrice(10.0);
        flower.setType(flowerType);

        FlowerInBouquet fib = new FlowerInBouquet();
        fib.setFlower(flower);
        fib.setQuantity(3);
        fib.setStemLength(40);

        AccessoryType accessoryType = new AccessoryType();
        accessoryType.setName("Стрічка");
        accessoryType.setBasePrice(5.0);

        Accessory accessory = new Accessory();
        accessory.setType(accessoryType);

        Bouquet bouquet = new Bouquet();
        bouquet.setName("Святковий букет");
        bouquet.setFlowers(List.of(fib));
        bouquet.setAccessories(List.of(accessory, accessory)); // 2 стрічки

        String testFileName = "test_receipt.pdf";

        // Виклик тестованого методу
        receiptPdfService.generatePdfReceipt(bouquet, testFileName);

        // Перевірка існування файлу
        File createdFile = new File("src/main/resources/com/example/kursova_flowers/pdf/" + testFileName);
        assertTrue(createdFile.exists(), "PDF файл має бути створений");

        // Чистимо створений файл
      /*  if (createdFile.exists()) {
            boolean deleted = createdFile.delete();
            assertTrue(deleted, "Тестовий файл має бути видалений після тесту");
        }*/
    }
}
