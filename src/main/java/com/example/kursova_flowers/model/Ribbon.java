package com.example.kursova_flowers.model;

public class Ribbon extends Accessory {
    private double width;

    public Ribbon() { super(); }
    public Ribbon(int id, Bouquet bouquet, AccessoryType type, String color, String note, double width) {
        super(id, bouquet, type, color, note);
        this.width = width;
    }
    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }
}
