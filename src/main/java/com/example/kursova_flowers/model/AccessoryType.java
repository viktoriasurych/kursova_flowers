package com.example.kursova_flowers.model;

public class AccessoryType {
    private int id;
    private String name;
    private double basePrice;

    public AccessoryType() {}
    public AccessoryType(int id, String name, double basePrice) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
    }
    // Геттери і сеттери...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
}




