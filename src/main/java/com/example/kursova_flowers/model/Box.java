package com.example.kursova_flowers.model;

public class Box extends Accessory {
    private String boxType;

    public Box() { super(); }
    public Box(int id, Bouquet bouquet, AccessoryType type, String color, String note, String boxType) {
        super(id, bouquet, type, color, note);
        this.boxType = boxType;
    }
    public String getBoxType() { return boxType; }
    public void setBoxType(String boxType) { this.boxType = boxType; }
}
