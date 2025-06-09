package com.example.kursova_flowers.model;

import java.time.LocalDate;

public class Flower {

    private int id;
    private String name;
    private FlowerType type;
    private double price;
    private LocalDate pickedDate;
    private int totalQuantity;

    public Flower() {}
    public Flower(int id, String name, FlowerType type, double price, LocalDate pickedDate, int totalQuantity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.price = price;
        this.pickedDate = pickedDate;
        this.totalQuantity = totalQuantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public FlowerType getType() { return type; }
    public void setType(FlowerType type) { this.type = type; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public LocalDate getPickedDate() { return pickedDate; }
    public void setPickedDate(LocalDate pickedDate) { this.pickedDate = pickedDate; }
    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }


    @Override
    public String toString() {
        return this.name;  // або інше поле, яке має відображатись
    }

}
