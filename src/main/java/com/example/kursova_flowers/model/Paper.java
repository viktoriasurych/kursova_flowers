package com.example.kursova_flowers.model;

public class Paper extends Accessory {
    private String material;

    public Paper() { super(); }
    public Paper(int id, Bouquet bouquet, AccessoryType type, String color, String note, String material) {
        super(id, bouquet, type, color, note);
        this.material = material;
    }
    public Paper(AccessoryType type, String color, String note, String material) {
        super(0, null, type, color, note);
        this.material = material;
    }
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
}
