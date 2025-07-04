package com.example.kursova_flowers.service;

import com.example.kursova_flowers.model.*;
import com.itextpdf.kernel.font.*;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReceiptPdfService {

    /**
     * Генерує PDF-файл із чеком для заданого букету.
     * Документ зберігається у відповідній папці ресурсу та відкривається після створення.
     *
     * @param bouquet  об'єкт {@link Bouquet}, який містить дані про квіти та аксесуари
     * @param filePath назва файлу PDF без шляху (лише ім’я з розширенням .pdf)
     * @throws IOException якщо виникла помилка під час створення PDF або відкриття файлу
     */
    public void generatePdfReceipt(Bouquet bouquet, String filePath) throws IOException {
        String pdfFolder = "src/main/resources/com/example/kursova_flowers/pdf/";

        PdfFont font = PdfFontFactory.createFont(
                "src/main/resources/com/example/kursova_flowers/fonts/OpenSans-VariableFont_wght.ttf",
                "Cp1251"
        );
        filePath = pdfFolder + filePath;

        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        Paragraph title = new Paragraph("Чек на букет: " + bouquet.getName())
                .setFont(font)
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);
        document.add(new Paragraph(" ").setFont(font));

        document.add(new Paragraph("Квіти:").setFontSize(14).setBold().setFont(font));

        Table flowerTable = new Table(UnitValue.createPercentArray(new float[]{2, 3, 2, 2, 2}))
                .useAllAvailableWidth();

        flowerTable.addHeaderCell(new Cell().add(new Paragraph("Тип").setFont(font)));
        flowerTable.addHeaderCell(new Cell().add(new Paragraph("Назва").setFont(font)));
        flowerTable.addHeaderCell(new Cell().add(new Paragraph("Довжина стебла").setFont(font)));
        flowerTable.addHeaderCell(new Cell().add(new Paragraph("Кількість").setFont(font)));
        flowerTable.addHeaderCell(new Cell().add(new Paragraph("Ціна").setFont(font)));

        for (FlowerInBouquet fib : bouquet.getFlowers()) {
            flowerTable.addCell(new Cell().add(new Paragraph(fib.getFlower().getType().getName()).setFont(font)));
            flowerTable.addCell(new Cell().add(new Paragraph(fib.getFlower().getName()).setFont(font)));
            flowerTable.addCell(new Cell().add(new Paragraph(String.valueOf(fib.getStemLength())).setFont(font)));
            flowerTable.addCell(new Cell().add(new Paragraph(String.valueOf(fib.getQuantity())).setFont(font)));
            flowerTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", fib.getQuantity() * fib.getFlower().getPrice())).setFont(font)));
        }
        document.add(flowerTable);
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Аксесуари:").setFontSize(14).setBold().setFont(font));

        Table accessoryTable = new Table(UnitValue.createPercentArray(new float[]{4, 2, 2}))
                .useAllAvailableWidth();

        accessoryTable.addHeaderCell(new Cell().add(new Paragraph("Тип назва").setFont(font)));
        accessoryTable.addHeaderCell(new Cell().add(new Paragraph("К-сть").setFont(font)));
        accessoryTable.addHeaderCell(new Cell().add(new Paragraph("Ціна").setFont(font)));

        Map<AccessoryType, Long> accessoryCounts = bouquet.getAccessories().stream()
                .collect(Collectors.groupingBy(Accessory::getType, Collectors.counting()));

        for (Map.Entry<AccessoryType, Long> entry : accessoryCounts.entrySet()) {
            AccessoryType type = entry.getKey();
            long count = entry.getValue();
            double totalPrice = type.getBasePrice() * count;

            accessoryTable.addCell(new Cell().add(new Paragraph(type.getName()).setFont(font)));
            accessoryTable.addCell(new Cell().add(new Paragraph(String.valueOf(count)).setFont(font)));
            accessoryTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", totalPrice)).setFont(font)));
        }

        document.add(accessoryTable);
        document.add(new Paragraph(" "));

        BouquetCalculatorService bouquetCalculatorService = new BouquetCalculatorService();
        double total = bouquetCalculatorService.calculateTotalPrice(bouquet);

        Paragraph totalPrice = new Paragraph("Загальна ціна: " + String.format("%.2f грн", total))
                .setFontSize(16)
                .setBold()
                .setFont(font);
        document.add(totalPrice);

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        Paragraph timestamp = new Paragraph("Дата створення чеку: " + now)
                .setFontSize(12)
                .setFont(font);
        document.add(timestamp);

        document.close();

        File pdfFile = new File(filePath);
        if (pdfFile.exists()) {
            Desktop.getDesktop().open(pdfFile);
        }
    }
}
