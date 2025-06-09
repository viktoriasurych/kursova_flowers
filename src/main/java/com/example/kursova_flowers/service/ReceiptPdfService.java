package com.example.kursova_flowers.service;

import com.example.kursova_flowers.model.Bouquet;
import com.example.kursova_flowers.model.FlowerInBouquet;
import com.example.kursova_flowers.model.Accessory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.font.PdfFontFactory;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ReceiptPdfService {

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

        // Заголовок з шрифтом, що підтримує кирилицю
        Paragraph title = new Paragraph("Чек на букет: " + bouquet.getName())
                .setFont(font)
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);
        document.add(new Paragraph(" ").setFont(font)); // порожній рядок

        // Квіти
        Paragraph flowersHeader = new Paragraph("Квіти:")
                .setFontSize(14)
                .setBold().setFont(font);
        document.add(flowersHeader);

        // Таблиця квітів - 5 колонок
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
        document.add(new Paragraph(" ")); // порожній рядок

        // Аксесуари
        Paragraph accessoriesHeader = new Paragraph("Аксесуари:")
                .setFontSize(14)
                .setBold().setFont(font);
        document.add(accessoriesHeader);

        Table accessoryTable = new Table(UnitValue.createPercentArray(new float[]{4, 4, 2}))
                .useAllAvailableWidth();

        accessoryTable.addHeaderCell(new Cell().add(new Paragraph("Тип").setFont(font)));
        accessoryTable.addHeaderCell(new Cell().add(new Paragraph("Ціна").setFont(font)));

        for (Accessory acc : bouquet.getAccessories()) {
            accessoryTable.addCell(new Cell().add(new Paragraph(acc.getType().getName()).setFont(font)));
            accessoryTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", acc.getType().getBasePrice())).setFont(font)));
        }
        document.add(accessoryTable);
        document.add(new Paragraph(" "));

        BouquetCalculatorService bouquetCalculatorService = new BouquetCalculatorService();

        // Загальна ціна
        Paragraph totalPrice = new Paragraph("Загальна ціна: " + String.format("%.2f грн", bouquetCalculatorService.calculateTotalPrice(bouquet)))
                .setFontSize(16)
                .setBold().setFont(font);
        document.add(totalPrice);

        document.close();

        File pdfFile = new File(filePath);
        if (pdfFile.exists()) {
            Desktop.getDesktop().open(pdfFile);
        }
    }

}
