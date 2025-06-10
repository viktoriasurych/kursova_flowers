package com.example.kursova_flowers.model;

public class Accessory {
    private int id;
    private Bouquet bouquet;
    private AccessoryType type;
    private String color;
    private String note;

    public Accessory() {}
    public Accessory(int id, Bouquet bouquet, AccessoryType type, String color, String note) {
        this.id = id;
        this.bouquet = bouquet;
        this.type = type;
        this.color = color;
        this.note = note;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Bouquet getBouquet() { return bouquet; }
    public void setBouquet(Bouquet bouquet) { this.bouquet = bouquet; }
    public AccessoryType getType() { return type; }
    public void setType(AccessoryType type) { this.type = type; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
