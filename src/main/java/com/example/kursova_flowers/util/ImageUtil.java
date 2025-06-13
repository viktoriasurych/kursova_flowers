package com.example.kursova_flowers.util;

import javafx.scene.image.Image;

import java.util.Random;

public class ImageUtil {
    private static final Random random = new Random();
    private static int maxImages = 15;

    /**
     * Повертає Image з випадковим фото з ресурсу /img/fw/fw-<number>.png,
     * де number - від 1 до maxImages включно.
     *
     * @return Image
     */
    public static Image getRandomBouquetImage() {
        int index = random.nextInt(maxImages) + 1;  // випадкове число від 1 до maxImages
        String path = String.format("/com/example/kursova_flowers/img/fw/fw-%d.png", index);
        try {
            return new Image(ImageUtil.class.getResource(path).toExternalForm());
        } catch (NullPointerException e) {
            System.err.println("Image not found: " + path);
            return null; // або можна повернути дефолтну картинку
        }
    }
}
