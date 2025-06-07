package com.example.kursova_flowers.model;

public class FlowerType {
    private int id;
    private String name;

    public FlowerType() {}
    public FlowerType(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return this.name;  // або інше поле, яке має відображатись
    }

}
