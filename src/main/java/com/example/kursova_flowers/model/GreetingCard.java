package com.example.kursova_flowers.model;

public class GreetingCard extends Accessory {
    private String text;

    public GreetingCard() { super(); }
    public GreetingCard(int id, Bouquet bouquet, AccessoryType type, String color, String note, String text) {
        super(id, bouquet, type, color, note);
        this.text = text;
    }
    public GreetingCard(AccessoryType type, String color, String note, String text) {
        super(0, null, type, color, note);
        this.text = text;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
